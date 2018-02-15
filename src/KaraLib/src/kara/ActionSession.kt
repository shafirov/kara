package kara

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * @author max
 */
interface ActionSession {
    val id: String

    fun instatiateHttpSession()
    fun getAttribute(key: String): Any?
    fun setAttribute(key: String, value: Any?)
    fun invalidate()
}

object NullSession : ActionSession {
    override val id = "none"
    override fun getAttribute(key: String): Any? = null
    override fun setAttribute(key: String, value: Any?) {}
    override fun instatiateHttpSession() { }
    override fun invalidate() {}
}

class HttpActionSession(val request: HttpServletRequest): ActionSession {
    private var _session: HttpSession? = null

    private fun getSession(): HttpSession = _session
        ?: request.getSession(true)!!.also {
            _session = it
        }

    override val id: String get() = getSession().id
    override fun getAttribute(key: String): Any? = getSession().getAttribute(key)
    override fun setAttribute(key: String, value: Any?) {
        getSession().setAttribute(key, value)
    }

    override fun instatiateHttpSession() { getSession() }

    override fun invalidate() {
        getSession().invalidate()
        _session = null
    }
}
