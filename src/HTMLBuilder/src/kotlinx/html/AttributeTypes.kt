package kotlinx.html

import kotlin.reflect.KProperty

abstract class Attribute<T>(val name: String) {
    operator fun getValue(tag: HtmlTag, property: KProperty<*>): T = decode(tag[name])
    operator open fun setValue(tag: HtmlTag, property: KProperty<*>, value: T) {
        tag[name] = encode(value)
    }

    abstract fun encode(t: T): String?
    abstract fun decode(s: String?): T
}

open class StringAttribute(name: String) : Attribute<String>(name) {
    override fun encode(t: String): String? = t // TODO: it actually might need HTML esaping
    override fun decode(s: String?): String = s!! // TODO: it actually might need decode
}

class TextAttribute(name: String) : StringAttribute(name)
class RegexpAttribute(name: String) : StringAttribute(name)
class IdAttribute(name: String) : StringAttribute(name)
class MimeAttribute(name: String) : StringAttribute(name)

class IntAttribute(name: String) : Attribute<Int>(name) {
    override fun encode(t: Int): String? = t.toString()
    override fun decode(s: String?): Int = s!!.toInt()
}

class BooleanAttribute(name: String, val trueValue: String = "true", val falseValue: String = "false") : Attribute<Boolean>(name) {
    override fun encode(t: Boolean): String? = if (t) trueValue else falseValue

    override fun decode(s: String?): Boolean = when (s) {
        trueValue -> true
        falseValue -> false
        else -> throw RuntimeException("Unknown value for $name=$s")
    }
}

class TickerAttribute(name: String) : Attribute<Boolean>(name) {
    override fun encode(t: Boolean): String? = null

    override fun decode(s: String?): Boolean = s != null

    operator override fun setValue(tag: HtmlTag, property: KProperty<*>, value: Boolean) {
        if (value) {
            super.setValue(tag, property, value)
        } else {
            tag.removeAttribute(name)
        }
    }
}

class LinkAttribute(name: String) : Attribute<Link>(name) {
    override fun encode(t: Link): String? = t.href()
    override fun decode(s: String?): Link = DirectLink(s!!)
}

interface StringEnum<T : Enum<T>> {
    val value: String
}

class EnumAttribute<T>(name: String, val klass: Class<T>) : Attribute<T>(name)
    where T : StringEnum<T>, T : Enum<T>
{
    override fun encode(t: T): String? = t.value

    override fun decode(s: String?): T =
        klass.enumConstants!!.firstOrNull { encode(it) == s } ?: throw RuntimeException("Can't decode '$s' as value of '${klass.name}'")
}

class MimeTypesAttribute(name: String) : Attribute<List<String>>(name) {
    override fun encode(t: List<String>): String? = t.joinToString(",")
    override fun decode(s: String?): List<String> = s!!.split(',').map { it.trim() }
}
