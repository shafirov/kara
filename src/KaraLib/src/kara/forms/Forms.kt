package kara.forms

import kotlin.reflect.KMutableMemberProperty
import java.util.ArrayList
import java.util.Date
import java.text.SimpleDateFormat
import java.text.ParseException
import kotlin.properties.Delegates
import java.util.regex.Pattern

/* Tagged Error */

data class FormError(val context: String, val message: String)

/* Binding Result */

trait BindingResult<T>

data class SuccessBinding<T>(val value: T) : BindingResult<T>

data class FailedBinding<T>(val errors: List<FormError>) : BindingResult<T>

/* Form Binders */

trait FormBinder<T, V> {
    fun bind(data: T, context: String): BindingResult<V>

    fun unbind(data: V): T
}

trait ValidationBinder<T, V> : FormBinder<T, V> {
    fun validating(validator: (V) -> List<String>) = object : FormBinder<T, V> {
        override fun bind(data: T, context: String): BindingResult<V> {
            val result = this@ValidationBinder.bind(data, context)

            return when (result) {
                is SuccessBinding -> {
                    val errors = validator(result.value)
                    if (errors.isNotEmpty()) FailedBinding(errors.map { FormError(context, it) }) else result
                }
                else -> result
            }
        }

        override fun unbind(data: V): T = this@ValidationBinder.unbind(data)
    }

    fun verifying(message: String, predicate: (V) -> Boolean) =
            validating { if (predicate(it)) listOf() else listOf(message) }
}

[suppress("BASE_WITH_NULLABLE_UPPER_BOUND", "UNCHECKED_CAST")]
fun <T, V> FormBinder<T, V?>.not(): FormBinder<T, V> {
    val callee = this

    return object : FormBinder<T, V> {
        override fun bind(data: T, context: String): BindingResult<V> {
            val result = callee.bind(data, context)

            return when (result) {
                is SuccessBinding -> SuccessBinding(result.value!!)
                else -> result as BindingResult<V>
            }
        }

        override fun unbind(data: V): T = callee.unbind(data)
    }
}

private fun formError<T>(context: String, message: String) = FailedBinding<T>(listOf(FormError(context, message)))

fun optional<T>(delegate: FormBinder<String?, T>) = object : FormBinder<String?, T> {
    override fun bind(data: String?, context: String): BindingResult<T> {
        if (data == null || data.isEmpty())
            return SuccessBinding<T>(null)

        return delegate.bind(data, context)
    }

    override fun unbind(data: T): String? = delegate.unbind(data)
}

fun required<T>(delegate: FormBinder<String?, T>) = object : FormBinder<String?, T> {
    override fun bind(data: String?, context: String): BindingResult<T> {
        if (data == null || data.isEmpty())
            return formError(context, "Field is required")

        return delegate.bind(data, context)
    }

    override fun unbind(data: T): String? = delegate.unbind(data)
}

fun number(min: Int = Integer.MIN_VALUE, max: Int = Integer.MAX_VALUE) = object : ValidationBinder<String?, Int?> {
    {
        if (min > max) throw IllegalArgumentException("max cannot be less than min")
    }

    override fun bind(data: String?, context: String): BindingResult<Int?> {
        return try {
            val number = Integer.parseInt(data)
            when {
                number < min -> formError(context, "Minimum value is $min")
                number > max -> formError(context, "Maximum value is $max")
                else -> SuccessBinding(number)
            }
        } catch (e: NumberFormatException) {
            formError(context, "Invalid number format")
        }
    }

    override fun unbind(data: Int?): String? = data?.toString()
}

val number = number()

fun text(min: Int = 0, max: Int = Integer.MAX_VALUE, trim: Boolean = false) = object : ValidationBinder<String?, String?> {
    {
        if (min > max) throw IllegalArgumentException("max cannot be less than min")
    }

    override fun bind(data: String?, context: String): BindingResult<String?> {
        val text = if (trim) data?.trim() else data

        return when {
            text?.length() ?: 0 < min -> formError(context, "Minimum length is $min")
            text?.length() ?: 0 > max -> formError(context, "Maximum length is $max")
            else -> SuccessBinding(data)
        }
    }

    override fun unbind(data: String?): String? = data
}

val text = text()

val notEmptyText = text(min = 1, trim = true)

fun date(format: String) = object : ValidationBinder<String?, Date?> {
    private val sdf = SimpleDateFormat(format)

    override fun bind(data: String?, context: String): BindingResult<Date?> {
        return try {
            SuccessBinding(sdf.parse(data))
        } catch (e: ParseException) {
            formError(context, "Invalid number format")
        }
    }

    override fun unbind(data: Date?): String? = when {
        data != null -> sdf.format(data)
        else -> null
    }
}

val email = text.verifying("invalid email address", {
    it?.matches("""^(?!\.)("([^"\r\\]|\\["\r\\])*"|([-a-zA-Z0-9!#$%&'*+/=?^_`{|}~]|(?<!\.)\.)*)(?<!\.)@[a-zA-Z0-9][\w\.-]*[a-zA-Z0-9]\.[a-zA-Z][a-zA-Z\.]*[a-zA-Z]$""") ?: false
})

inline fun enum<reified T : Enum<T>>() = object : ValidationBinder<String?, T?> {
    override fun bind(data: String?, context: String): BindingResult<T?> {
        val value = javaClass<T>().getEnumConstants().firstOrNull { it.name() == data }

        return when {
            value == null -> formError(context, "Invalid enum value")
            else -> SuccessBinding(value)
        }
    }

    override fun unbind(data: T?): String? = data?.name()
}

val checked = object : ValidationBinder<String?, Boolean?> {
    override fun bind(data: String?, context: String): BindingResult<Boolean?> =
            SuccessBinding(data != null)

    override fun unbind(data: Boolean?): String? = if (data != null) "TRUE" else null
}

val boolean = checked

fun ignored<T>(value: T) = object : FormBinder<String?, T> {
    override fun bind(data: String?, context: String) = SuccessBinding(value)

    override fun unbind(data: T) = value.toString()
}

fun binding<T>(bind: (String?) -> T, unbind: (T) -> String = { it.toString() }, errorMessage: String = "Invalid value") =
        object : ValidationBinder<String?, T> {
            override fun bind(data: String?, context: String): BindingResult<T> = try {
                SuccessBinding(bind(data))
            } catch (e: Exception) {
                formError(context, errorMessage)
            }

            override fun unbind(data: T) = unbind(data)
        }

/* Form Handling */

trait FormField<T> {
    val fieldName: String
    val value: String?
    val errors: List<FormError>

    fun hasErrors() = errors.isNotEmpty()

    fun get<F>(field: KMutableMemberProperty<T, F?>): FormField<F>
}

data class Form<T>(val mapping: FormBinder<Map<String, String?>, T>,
                   val data: Map<String, String?> = mapOf(),
                   val errors: List<FormError> = listOf(),
                   val value: T = null) {
    private val unbound: Map<String, String?> by Delegates.blockingLazy {
        if (value != null)
            mapping.unbind(value)
        else
            throw IllegalStateException("form doesn't have a value")
    }

    val hasErrors = errors.isNotEmpty()
    val globalErrors = errors.filter { it.context == "" }
    val hasGlobalErrors = globalErrors.isNotEmpty()

    fun errors(code: String) = errors.filter { it.context == code }

    fun hasErrors(code: String) = errors(code).isNotEmpty()

    fun withError(code: String, message: String) =
            Form(mapping, data, errors + FormError(code, message), value)

    fun discardingErrors() = Form(mapping, data, value = value)

    fun bind(data: Map<String, String?>): Form<T> {
        val result = mapping.bind(data, "")

        return when (result) {
            is SuccessBinding -> Form(mapping, data, value = result.value)
            is FailedBinding -> Form(mapping, data, result.errors)
            else -> throw IllegalArgumentException("unsupported binding: " + result)
        }
    }

    /* todo fun bind(request: HttpServletRequest): Form<T> =
            bind(request.getParameterMap().flatMap {
                val param = it.key
                val values = it.value

                when {
                    values == null -> listOf(param to null)
                    values.size() == 1 -> listOf(param to values[0])
                    else ->
                        values.mapIndexed {(i, value) ->
                            "$param[$i]" to value
                        }
                }
            }.toMap())*/

    fun fill(value: T) = Form(mapping, data, errors, value)

    fun validate() = Form(mapping).bind(unbound)

    inner class FormFieldImpl<T>(override val fieldName: String) : FormField<T> {
        override val value: String? = if (this@Form.value != null) unbound[fieldName] else data[fieldName]
        override val errors: List<FormError> = errors(fieldName)
        override fun <F> get(field: KMutableMemberProperty<T, F?>) =
                FormFieldImpl<F>("$fieldName.${field.name}")

        override fun toString() =
                "FormField{name=$fieldName; value=$value; errors=$errors}"
    }

    fun get<F>(field: KMutableMemberProperty<T, F?>): FormField<F> =
            FormFieldImpl(field.name)
}

class BeanFieldsMapping<T>(private val mapping: Collection<FieldMapping<T, *>>,
                           private val factory: () -> T) : ValidationBinder<Map<String, String?>, T> {

    override fun unbind(data: T): Map<String, String?> {
        val result = hashMapOf<String, String?>()

        mapping.forEach {
            result.putAll(it.unbind(data))
        }

        return result
    }

    override fun bind(data: Map<String, String?>, context: String): BindingResult<T> {
        val result = factory()

        val errors = ArrayList<FormError>()

        mapping.forEach {
            it.bind(data, result, context(context, it.field.name), errors)
        }

        return when {
            errors.isEmpty() -> SuccessBinding(result)
            else -> FailedBinding(errors)
        }
    }

    private fun context(parent: String, child: String) =
            if (parent.isNotEmpty()) "$parent.$child" else child
}

fun mapping<T>(vararg mappings: FieldMapping<T, *>, factory: () -> T) =
        BeanFieldsMapping(listOf(*mappings), factory)

trait FieldMapping<Bean, Field> {
    val field: KMutableMemberProperty<Bean, Field>
    val binder: FormBinder<*, Field>

    fun bind(data: Map<String, String?>, context: String): BindingResult<Field>

    fun bind(data: Map<String, String?>, obj: Bean, context: String, errors: MutableCollection<FormError>) {
        val bindingResult = bind(data, context)

        when (bindingResult) {
            is SuccessBinding ->
                field.set(obj, bindingResult.value)
            is FailedBinding ->
                errors.addAll(bindingResult.errors)
        }
    }

    fun unbind(obj: Bean): Map<String, String?>
}

class SimpleMapping<Bean, Field>(override val field: KMutableMemberProperty<Bean, Field>,
                                 override val binder: FormBinder<String?, Field>) : FieldMapping<Bean, Field> {
    override fun unbind(obj: Bean): Map<String, String?> =
            mapOf(field.name to binder.unbind(field.get(obj)))

    override fun bind(data: Map<String, String?>, context: String) = binder.bind(data[context], context)
}

class ComplexMapping<Bean, Field>(override val field: KMutableMemberProperty<Bean, Field>,
                                  override val binder: FormBinder<Map<String, String?>, Field>) : FieldMapping<Bean, Field> {
    override fun unbind(obj: Bean): Map<String, String?> =
            binder.unbind(field.get(obj)) mapKeys { "${field.name}.${it.key}" }

    override fun bind(data: Map<String, String?>, context: String) = binder.bind(data, context)
}

fun <Bean, Field> KMutableMemberProperty<Bean, Field>.mapTo(binder: FormBinder<String?, Field>) =
        SimpleMapping(this, binder)

fun <Bean, Type> KMutableMemberProperty<Bean, List<Type>?>.toList(binder: FormBinder<String?, Type>) =
        ComplexMapping(this, object : FormBinder<Map<String, String?>, List<Type>?> {
            override fun bind(data: Map<String, String?>, context: String): BindingResult<List<Type>?> {
                val pattern = Pattern.compile(Pattern.quote(context) + """\[(?<index>\d+)\]""")

                val matched = sortedMapOf<Int, BindingResult<Type>>()

                data.forEach {
                    val matcher = pattern.matcher(it.key)

                    if (matcher.matches())
                        matched[matcher.group("index").toInt()] = binder.bind(it.value, it.key)
                }

                val bindings = matched.values()

                if (bindings.all { it is SuccessBinding })
                    return SuccessBinding(bindings.map { (it as SuccessBinding).value })

                return FailedBinding(
                        bindings.filter { it is FailedBinding }
                                .flatMap { (it as FailedBinding).errors }
                )
            }

            override fun unbind(data: List<Type>?): Map<String, String?> =
                    data?.mapIndexed {(index, value) ->
                        "[$index]" to binder.unbind(value)
                    }?.toMap() ?: mapOf()
        })

[suppress("BASE_WITH_NULLABLE_UPPER_BOUND")]
fun <Bean, Field> KMutableMemberProperty<Bean, Field?>.toNested(binder: FormBinder<Map<String, String?>, Field>) =
        ComplexMapping(this, nullableFieldBinderWrapper(binder, mapOf()))

[suppress("BASE_WITH_NULLABLE_UPPER_BOUND")]
private fun nullableFieldBinderWrapper<T, V>(binder: FormBinder<T, V>, nullValue: T): FormBinder<T, V?> =
        object : FormBinder<T, V?> {
            override fun unbind(data: V?): T = when {
                data == null -> nullValue
                else -> binder.unbind(data)
            }

            [suppress("UNCHECKED_CAST")]
            override fun bind(data: T, context: String): BindingResult<V?> =
                    binder.bind(data, context) as BindingResult<V?>
        }