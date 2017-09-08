package kara.internal

import kara.RouteParameters

fun String.toPathComponents(): List<String> = (if (length > 1) trimEnd('/') else this).split("/").filter { it.isNotEmpty() }
fun String.toRouteComponents(): List<RouteComponent> = toPathComponents().map { RouteComponent.create(it) }

/** Base class for objects that represent a single component of a route. */
abstract class RouteComponent(val componentText: String) {
    companion object {
        fun create(component: String): RouteComponent = when {
            component.length > 1 && component[0] == ':' && component.lastIndexOf(':') == 0 ->
                ParamRouteComponent(component)
            component.length > 1 && component[0] == '?' && component.lastIndexOf('?') == 0 ->
                OptionalParamRouteComponent(component)
            component == "*" ->
                WildcardRouteComponent(component)
            else ->
                StringRouteComponent(component)
        }
    }

    abstract fun matches(value: String): Boolean

    abstract fun setParameter(params: RouteParameters, component: String)
}

/** Route component for a literal string. */
class StringRouteComponent(componentText: String) : RouteComponent(componentText) {
    override fun matches(value: String): Boolean = value.equals(componentText, ignoreCase = true)

    override fun setParameter(params: RouteParameters, component: String) {
    }
}

/** Route component for a named parameter. */
class ParamRouteComponent(componentText: String) : RouteComponent(componentText) {
    val name = componentText.substring(1)

    override fun matches(value: String): Boolean = true

    override fun setParameter(params: RouteParameters, component: String) {
        params[name] = component
    }
}

/** Route component for a named parameter. */
class OptionalParamRouteComponent(componentText: String) : RouteComponent(componentText) {
    val name = componentText.substring(1)

    override fun matches(value: String): Boolean = true

    override fun setParameter(params: RouteParameters, component: String) {
        params[name] = component
    }
}

/** Route component for an unnamed parameter. */
class WildcardRouteComponent(componentText: String) : RouteComponent(componentText) {

    override fun matches(value: String): Boolean = true

    override fun setParameter(params: RouteParameters, component: String) {
        params.append(component)
    }
}
