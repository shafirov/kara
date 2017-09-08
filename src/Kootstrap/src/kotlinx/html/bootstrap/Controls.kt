package kotlinx.html.bootstrap

import kotlinx.html.*

val form_horizontal = "form-horizontal"
val form_control = "form-control"

fun HtmlBodyTag.blockAction(h: highlight = highlight.default, body: A.()->Unit) = a {
    addClass("btn btn-block btn-${h.name}")
    body()
}

fun HtmlBodyTag.action(h: highlight = highlight.default,
                              size: caliber = caliber.default,
                              body: A.()->Unit) = a {
    addClass("btn")
    if (size.value.isNotEmpty()) {
        addClass("btn-${size.value}")
    }
    if (h != highlight.default) {
        addClass("btn-${h.name}")
    }
    body()
}

fun HtmlBodyTag.action(url: Link, h: highlight = highlight.default, size: caliber = caliber.default, body: A.()->Unit) = action(h, size) { href = url; body() }

fun HtmlBodyTag.bt_button(h: highlight = highlight.default, size: caliber = caliber.default, body: BUTTON.()->Unit) = button {
    addClass("btn")

    if (size.value.isNotEmpty()) addClass("btn-${size.value}")
    addClass("btn-${h.name}")

    body()
}

inline fun HtmlBodyTag.actionGroup(body: HtmlBodyTag.()->Unit) = p {
    body()
}

inline fun HtmlBodyTag.blockButton(h: highlight, body: BUTTON.()->Unit) = button {
    addClass("btn btn-block btn-${h.name}")
    body()
}

inline fun HtmlBodyTag.controlWithIcon(iconName: String, body: DIV.()->Unit) = div("input-group") { span("input-group-addon") { icon(iconName) }; body() }
inline fun HtmlBodyTag.controlGroup(body: DIV.()->Unit) = div("form-group", body)

inline fun HtmlBodyTag.controlLabel(body: LABEL.()->Unit) = label("control-label", body)

fun HtmlBodyTag.icon(name : String, c: String?=null)  = i {
    addClass("icon icon-$name")
    if (c != null) {
        addClass(c)
    }
}
