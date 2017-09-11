package kara

import kara.internal.ResourceDescriptor
import kara.internal.ResourceDispatcher
import kotlinx.reflection.MissingArgumentException
import kotlinx.reflection.filterIsAssignable
import kotlinx.reflection.findClasses
import kotlinx.reflection.kotlinCached
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.net.SocketException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KAnnotatedElement

/** Current application execution context
 */
class ApplicationContext(val config: ApplicationConfig,
                         packages: List<String>,
                         val classLoader: ClassLoader,
                         val reflectionCache: MutableMap<Pair<Int, String>, List<Class<*>>>,
                         val resourceTypes: List<Pair<KAnnotatedElement, ResourceDescriptor>>) {
    val logger = LoggerFactory.getLogger(this.javaClass)!!
    private val interceptors = ArrayList<Interceptor>()
    private val monitorInstances = ArrayList<ApplicationContextMonitor>()
    private val maskedParameterNames = config.tryGet("kara.parameters.logging.masked")?.split(",")?.map { it.trim() }.orEmpty()

    val version: Int = ++versionCounter

    init {
        packages.flatMap { scanPackageForMonitors(it) }.map {
            it.kotlinCached.objectInstance ?: it.newInstance()
        }.sortedBy { it.priority }.forEach { monitor ->
            logger.info("Executing startup sequence on ${monitor.javaClass}")
            monitor.created(this)
            monitorInstances.add(monitor)
        }
    }

    fun intercept(interceptor: (request: HttpServletRequest, response: HttpServletResponse,
                                descriptor: ResourceDescriptor?,
                                proceed: (HttpServletRequest, HttpServletResponse, ResourceDescriptor?) -> Boolean) -> Boolean) {
        intercept(object : Interceptor(interceptors.size) {
            override fun invoke(request: HttpServletRequest, response: HttpServletResponse, descriptor: ResourceDescriptor?,
                                proceed: (HttpServletRequest, HttpServletResponse, ResourceDescriptor?) -> Boolean): Boolean {
                return interceptor(request, response, descriptor, proceed)
            }
        })
    }

    fun intercept(interceptor: Interceptor) {
        interceptors.add(interceptor)
        interceptors.sort()
    }

    fun dispatch(request: HttpServletRequest, response: HttpServletResponse): Boolean {

        fun formatLogErrorMsg(error: String, req: HttpServletRequest) : String {
            val paramAndValues = req.parameterMap.toList().
                    joinToString(prefix = "{", postfix = "}") { (name, value) ->
                        val valueStr = when {
                            name in maskedParameterNames -> "*****"
                            value.size == 1 -> value[0]
                            else -> value.joinToString(prefix = "[", postfix = "]", separator = ",")
                        }
                        "$name: $valueStr"
            }
            return "$error processing ${req.method} ${req.requestURL}, parameters=$paramAndValues, " +
                    "User agent: ${req.getHeader("User-Agent")}, Referer: ${req.getHeader("Referer")}"
        }

        fun dispatch(index: Int, request: HttpServletRequest, response: HttpServletResponse, resourceDescriptor: ResourceDescriptor?): Boolean {
            return if (index in interceptors.indices) {
                interceptors[index](request, response, resourceDescriptor) { req, resp, desc -> dispatch(index + 1, req, resp, desc) }
            }
            else {
                dispatcher.dispatch(request, response, resourceDescriptor)
            }
        }

        try {
            val url = request.requestURI
            val resourceDescriptor = dispatcher.findDescriptor(request.method, url.removePrefix(request.contextPath.orEmpty()))
            return dispatch(0, request, response, resourceDescriptor)
        }
        catch(ex: SocketException) {
            // All kinds of EOFs and Broken Pipes can be safely ignored
        }
        catch(e400: MissingArgumentException) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e400.message)
            Application.logger.warn(formatLogErrorMsg("400", request), e400)
        } catch(e400: InvalidRequestException) {
            Application.logger.warn(formatLogErrorMsg("400", request), e400)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e400.message)
        } catch(e404: NotFoundException) {
            Application.logger.warn(formatLogErrorMsg("404", request), e404)
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e404.message)
        } catch(e405: UnknownHttpMethodException) {
            Application.logger.warn(formatLogErrorMsg("405", request), e405)
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, e405.message)
        }
        catch(ex: Throwable) {
            when {
                ex.javaClass.name == "org.apache.catalina.connector.ClientAbortException" -> {} // do nothing for tomcat specific exception
                else -> {
                    Application.logger.error(formatLogErrorMsg("Error", request), ex)
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.message)
                }
            }
        }

        return true
    }

    fun dispose() = monitorInstances.forEach { it.destroyed(this) }

    private var _dispatcher: ResourceDispatcher? = null

    val dispatcher: ResourceDispatcher
        get() {
            var d = _dispatcher
            if (d == null) {
                d = ResourceDispatcher(this, resourceTypes)
                _dispatcher = d
            }
            return d
        }


    fun scanPackageForMonitors(prefix: String): List<Class<ApplicationContextMonitor>> = try {
        classLoader.findClasses(prefix, reflectionCache).
                filterIsAssignable<ApplicationContextMonitor>().
                filter { !Modifier.isAbstract(it.modifiers) }.
                distinct()
    } catch(e: Throwable) {
        e.printStackTrace()
        listOf()
    }

    companion object {
        var versionCounter: Int = 0
    }
}
