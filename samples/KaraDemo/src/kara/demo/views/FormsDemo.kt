package kara.demo.views

import kara.HtmlTemplateView
import kara.Template
import kotlin.html.*
import kara.forms.Form
import kara.forms.FormField
import kara.demo.routes.forms.User
import kara.demo.routes.forms.FormsDemo
import kara.demo.routes.forms.Address
import kara.demo.routes.forms.Gender

fun ShowDemoForm(form: Form<User>) = HtmlTemplateView(FormTemplate(form), {})

private fun FORM.inputTmpl<T>(field: FormField<T>, label: String, input: HtmlBodyTag.() -> Unit) {
    div {
        style = "margin: 10px;"

        label {
            +label

            input()
        }

        if (field.hasErrors()) {
            ul {
                field.errors.forEach {
                    li {
                        +it.message
                    }
                }
            }
        }
    }
}

private fun FORM.inputText<T>(field: FormField<T>, label: String) = inputTmpl(field, label) {
    input {
        inputType = InputType.text
        name = field.fieldName
        value = field.value.orEmpty()
    }
}

private fun FORM.checkbox(field: FormField<Boolean?>, label: String) = inputTmpl(field, label) {
    input {
        inputType = InputType.checkbox
        name = field.fieldName
        value = field.value.orEmpty()

        if (field.value != null)
            checked = true
    }
}

private fun FORM.radio<T: Enum<T>>(field: FormField<T>, values: Array<T>, label: String) = inputTmpl(field, label) {
    values.forEach {(radioValue) ->
        label {
            input {
                inputType = InputType.radio
                name = field.fieldName
                value = radioValue.toString()

                if (radioValue.name() == field.value)
                    checked = true
            }
            +radioValue.name()
        }
    }
}

private class FormTemplate(val form: Form<User>) : Template<HTML>() {
    override fun HTML.render() {
        head {
            title {
                +"Kara forms binding demo"
            }
        }

        body {
            if (form.hasGlobalErrors) {
                ul {
                    form.globalErrors.forEach {
                        li {
                            +it.message
                        }
                    }
                }
            }

            form {
                action = FormsDemo.SaveUserAction()
                method = FormMethod.post

                inputText(form[User::firstName], "First Name: ")
                inputText(form[User::lastName], "Last Name: ")

                inputText(form[User::age], "Age: ")

                inputText(form[User::addr][Address::city], "City: ")
                inputText(form[User::addr][Address::street], "Street: ")

                radio(form[User::gender], Gender.values(), "Gender: ")

                checkbox(form[User::registered], "Registered: ")

                br()

                input {
                    inputType = InputType.submit
                }
            }
        }
    }
}
