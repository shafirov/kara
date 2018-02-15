package kara.internal

import kara.ApplicationContext
import kara.InvalidRouteException
import kara.asHttpMethod
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KAnnotatedElement

/** Used by the server to dispatch requests to their appropriate actions.
 */
class ResourceDispatcher(val context: ApplicationContext, resourceTypes: List<Pair<KAnnotatedElement, ResourceDescriptor>>) {

    private val resources = resourceTypes.toMap()

    private val httpMethods = resources.values.groupBy { it.httpMethod }.mapValues { (method, descriptors) ->
        descriptors.groupBy { it.route }.mapValues { (route, descList) ->
            descList.singleOrNull() ?: error("Route [$method - '$route'] has ${descList.size} descriptors, but only 1 is expected!")
        }
    }

    fun route(requestType: KAnnotatedElement): ResourceDescriptor = resources[requestType] ?: requestType.route()

    /** Matches an http method and url to an ActionInfo object. Returns null if no match is found.
     */
    fun findDescriptor(httpMethod: String, url: String): ResourceDescriptor? {
        val matches = httpMethods[httpMethod.asHttpMethod()]?.filterValues { it.matches(url) }?.values.orEmpty()

        return when (matches.size) {
            1 -> matches.single()
            0 -> null
            else -> throw InvalidRouteException("URL '$url' matches more than single route: ${matches.joinToString { it.route }}")
        }
    }

    fun dispatch(request: HttpServletRequest, response: HttpServletResponse, resourceDescriptor: ResourceDescriptor?): Boolean {
        if (resourceDescriptor != null) {
            resourceDescriptor.exec(context, request, response)
            return true
        }
        return false
    }
}
