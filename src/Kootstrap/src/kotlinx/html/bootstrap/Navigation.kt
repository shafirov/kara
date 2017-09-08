package kotlinx.html.bootstrap

import kotlinx.html.*

fun ListTag.item(active: Boolean = false, body: LI.()->Unit) = li(if (active) "active" else "", contents = body)
fun ListTag.item(url: Link, active: Boolean = false, body: A.()->Unit) = item(active) { a { href = url; body() } }
fun ListTag.item(name: String, active: Boolean = false, body: A.()->Unit) = item(active) { a { href = DirectLink("#$name"); body() } }
fun ListTag.itemDivider() = li("divider") {}

fun ListTag.dropdownItem(c:StyleClass? = null, caption: A.()->Unit, items: UL.()->Unit) = li {
    addClass("dropdown")
    addClass(c)
    a {
        this["href"] = "#"
        this["data-toggle"] = "dropdown"
        caption()
    }
    menu {
        items()
    }
}


inline fun HtmlBodyTag.listGroup(body: UL.()->Unit) = ul("list-group", contents = body)
fun ListTag.listItem(active: Boolean = false, body: LI.()->Unit) = item(active) {
    addClass("list-group-item")
    body()
}

fun ListTag.listItem(url: Link, active: Boolean = false, body: A.()->Unit) = item(url, active) {
    addClass("list-group-item")
    body()
}
fun ListTag.listDivider() = span("list-group-divider") {}

inline fun HtmlBodyTag.linkGroup(body: DIV.()->Unit) = div("list-group", contents = body)
fun DIV.linkItem(active: Boolean = false, body: A.()->Unit) = a(if (active) "list-group-item active" else "list-group-item", contents = body)
fun DIV.linkItem(url: Link, active: Boolean = false, body: A.()->Unit) = linkItem(active) { href = url; body() }
fun DIV.linkDivider() = span("list-group-divider") {}

inline  fun HtmlBodyTag.menu(body: UL.()->Unit) = ul("dropdown-menu", contents = body)

enum class navbarPosition {
    `default`, top, bottom, static
}

fun HtmlBodyTag.navbar(position: navbarPosition, body: DIV.()->Unit) = div("navbar navbar-default" + when(position) {
    navbarPosition.top -> " navbar-fixed-top"
    navbarPosition.bottom -> " navbar-fixed-bottom"
    navbarPosition.static -> " navbar-static-top"
    else -> ""
}, contents = body)

inline fun HtmlBodyTag.navbarHeader(body: DIV.()->Unit) = div("navbar-header", contents = body)
inline fun HtmlBodyTag.navbarBrand(body: A.()->Unit) = a("navbar-brand", contents = body)
inline fun HtmlBodyTag.navbarCollapse(body: DIV.()->Unit) = div("collapse navbar-collapse navbar-menu-collapse", contents = body)
inline fun HtmlBodyTag.navbarCollapseToggle(body: BUTTON.()->Unit) = button {
    addClass("navbar-toggle")
    buttonType = ButtonType.button
    attribute("data-toggle", "collapse")
    attribute("data-target", ".navbar-menu-collapse")
    span("sr-only") { +"Toggle navigation" }
    body()
}
inline fun HtmlBodyTag.navbarGroup(body: UL.()->Unit) = ul("nav navbar-nav", contents = body)
inline fun HtmlBodyTag.navbarGroupRight(body: UL.()->Unit) = ul("nav navbar-nav navbar-right pull-right", contents = body)
inline fun HtmlBodyTag.navbarGroupLeft(body: UL.()->Unit) = ul("nav navbar-nav navbar-left", contents = body)

inline fun HtmlBodyTag.breadcrumb(body: UL.()->Unit) = ul("breadcrumb", contents = body)
