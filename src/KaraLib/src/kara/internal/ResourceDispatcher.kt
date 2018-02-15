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

    private val httpMethods = resources.values.groupBy { it.httpMethod }
    
    init {
        val errors = httpMethods.flatMap { (method, descriptors) ->
            descriptors.groupingBy { it.route }.eachCount().filter { it.value > 1 }.map { Triple(method, it.key, it.value) }
        }
        if (errors.isNotEmpty()) {
            val error = errors.joinToString { (method, route, count) ->
                "\r\n\tRoute [$method - '$route'] has $count descriptors, but only 1 is expected!"
            }
            error(error)
        }
    }

    fun route(requestType: KAnnotatedElement): ResourceDescriptor = resources[requestType] ?: requestType.route()

    /** Matches an http method and url to an ActionInfo object. Returns null if no match is found.
     */
    fun findDescriptor(httpMethod: String, url: String): ResourceDescriptor? {
        val matches = httpMethods[httpMethod.asHttpMethod()]?.filter { it.matches(url) }.orEmpty()

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
