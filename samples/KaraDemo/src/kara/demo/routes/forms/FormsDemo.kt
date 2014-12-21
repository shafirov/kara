package kara.demo.routes.forms

import kara.Get
import kara.Request
import kara.Post
import kara.TextResult
import kara.forms.*
import kara.demo.views

enum class Gender { MAIL FEMAIL }

data class Address(
        var city: String? = null,
        var street: String? = null
)

data class User(
        var firstName: String? = null,
        var lastName: String? = null,
        var age: Int? = null,
        var gender: Gender? = null,
        var addr: Address? = null,
        var registered: Boolean? = null
)

object FormsDemo {
    val UserForm = Form(
            mapping(
                    User::firstName mapTo notEmptyText,
                    User::lastName mapTo notEmptyText,
                    User::age mapTo number(min = 18).verifying("21 is restricted age") { it != 21 },
                    User::gender mapTo enum<Gender>(),
                    User::registered mapTo boolean,
                    User::addr toNested mapping (
                            Address::city mapTo text(min = 5),
                            Address::street mapTo text(min = 5, max = 10).verifying("Wrong format") {
                                it!!.endsWith("st.")
                            }
                    ) { Address() }
                            .verifying("Street address must be prefixed by city") { it.street!!.startsWith(it.city!!) }
            ) { User() }
                    .verifying("first name and last name MUST be different") {
                        it.firstName != it.lastName
                    }
    )

    Get("/user")
    class UserFormAction : Request({
        val form = UserForm fill User(firstName = "John", age = 21)

        views.ShowDemoForm(form)
    })

    Get("/user/done")
    class UserSaveCompleteAction : Request({
        TextResult("The user has been saved successfully")
    })

    Post("/user")
    class SaveUserAction : Request({
        val form = UserForm bind params._map

        when {
            form.hasErrors -> views.ShowDemoForm(form)
            else -> redirect(UserSaveCompleteAction())
        }
    })
}