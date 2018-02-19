package kara

import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * @author max
 */
interface ActionSession {
    val id: String

    fun getAttribute(key: String): Any?
    fun setAttribute(key: String, value: Any?)
    fun invalidate()
    fun flush()
}

object NullSession : ActionSession {
    override val id = "none"
    override fun getAttribute(key: String): Any? = null
    override fun setAttribute(key: String, value: Any?) {}
    override fun invalidate() {}
    override fun flush() {}
}

class HttpActionSession(val request: HttpServletRequest): ActionSession {
    private val sessionCache = HashMap<String, Any?>()
    private var _session: HttpSession? = null

    private fun getSession(force: Boolean): HttpSession? = _session
        ?: request.getSession(force)?.also {
            _session = it
        }

    override val id: String get() = getSession(true)!!.id

    override fun getAttribute(key: String): Any? = getSession(false)?.let{ session ->
        if (key in sessionCache)
            sessionCache[key]
        else
            session.getAttribute(key)
    }

    override fun setAttribute(key: String, value: Any?) {
        if (_session == null) {
            getSession(true)
        }
        sessionCache[key] = value
    }

    override fun invalidate() {
        getSession(false)?.invalidate()
        sessionCache.clear()
        _session = null
    }

    override fun flush() {
        if (sessionCache.isNotEmpty()) {
            _session?.let { session ->
                sessionCache.forEach { (key, value) ->
                    session.setAttribute(key, value)
                }
            } ?: error("No session initialized.")
            sessionCache.clear()
        }
    }
}
