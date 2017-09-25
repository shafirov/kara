package kara.tests.mock

import kara.internal.partition
import kotlinx.reflection.RECORD_SEPARATOR_CHAR
import kotlinx.reflection.urlDecode
import java.io.BufferedReader
import java.security.Principal
import java.util.*
import javax.servlet.RequestDispatcher
import javax.servlet.ServletInputStream
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.servlet.http.HttpUpgradeHandler
import kotlin.collections.ArrayList


class MockHttpServletRequest(method : String, url : String) : HttpServletRequest {

    private var _method : String = method
    private var _url : String = url
    private val params = Hashtable<String,String>()
    private val headers = ArrayList<Pair<String, String>>()

    init {
        url.split('?').getOrNull(1)?.split('&')?.map{ urlDecode(it).partition('=') }?.groupBy { it.first }?.forEach {
            params[it.key] = it.value.joinToString(separator = RECORD_SEPARATOR_CHAR) { it.second }
        }
    }

    override fun startAsync(p0 : javax.servlet.ServletRequest?, p1 : javax.servlet.ServletResponse?) : javax.servlet.AsyncContext? {
        throw UnsupportedOperationException()
    }
    override fun startAsync() : javax.servlet.AsyncContext? {
        throw UnsupportedOperationException()
    }
    override fun getPart(p0 : String?) : javax.servlet.http.Part? {
        throw UnsupportedOperationException()
    }
    override fun isAsyncSupported() : Boolean {
        return false
    }
    override fun isAsyncStarted() : Boolean {
        return false
    }
    override fun authenticate(p0 : javax.servlet.http.HttpServletResponse?) : Boolean {
        return false
    }
    override fun logout()  {
    }
    override fun login(p0 : String?, p1 : String?)  {
    }
    override fun getAsyncContext() : javax.servlet.AsyncContext? {
        throw UnsupportedOperationException()
    }
    override fun getDispatcherType() : javax.servlet.DispatcherType? {
        throw UnsupportedOperationException()
    }
    override fun getServletContext() : javax.servlet.ServletContext? {
        throw UnsupportedOperationException()
    }
    override fun getParts() :MutableCollection<javax.servlet.http.Part>? {
        throw UnsupportedOperationException()
    }

    override fun getUserPrincipal() : Principal? {
        throw UnsupportedOperationException()
    }
    override fun getPathInfo() : String? {
        throw UnsupportedOperationException()
    }
    override fun getSession() : HttpSession? {
        throw UnsupportedOperationException()
    }

    private val session = MockHttpSession()

    override fun getSession(p0 : Boolean) : HttpSession? =        session

    override fun getHeaders(p0 : String?) : Enumeration<String>? =
        p0?.let { Collections.enumeration(headers.filter { it.first.equals(p0, true) }.map { it.second }) }

    override fun isRequestedSessionIdFromCookie() : Boolean {
        throw UnsupportedOperationException()
    }
    override fun getHeaderNames() : Enumeration<String>? = Collections.enumeration(headers.map { it.first }.distinct())

    override fun getRemoteUser() : String? {
        throw UnsupportedOperationException()
    }
    override fun getMethod() : String? {
        return _method
    }
    override fun getServletPath() : String? {
        throw UnsupportedOperationException()
    }
    override fun getRequestURL() : StringBuffer? {
        return StringBuffer(_url)
    }
    override fun isUserInRole(p0 : String?) : Boolean {
        throw UnsupportedOperationException()
    }
    override fun getCookies() : Array<Cookie>? {
        return emptyArray()
    }
    override fun getRequestURI() : String? {
        return _url
    }
    override fun getQueryString() : String? {
        val comps = _url.split("?")
        if (comps.size > 1)
            return comps[1]
        return null
    }
    override fun isRequestedSessionIdValid() : Boolean {
        throw UnsupportedOperationException()
    }
    override fun getPathTranslated() : String? {
        throw UnsupportedOperationException()
    }
    override fun isRequestedSessionIdFromUrl() : Boolean {
        throw UnsupportedOperationException()
    }
    override fun getContextPath() : String? {
        return ""
    }
    override fun getIntHeader(p0 : String?) : Int = getHeader(p0)?.toIntOrNull() ?: -1
    
    override fun getDateHeader(p0 : String?) : Long {
        throw UnsupportedOperationException()
    }
    override fun getAuthType() : String? {
        throw UnsupportedOperationException()
    }
    
    fun addHeader(header: String, value: String) = headers.add(header to value)
    
    override fun getHeader(p0 : String?) : String? = headers.firstOrNull { it.first.equals(p0, true) }?.second

    override fun isRequestedSessionIdFromURL() : Boolean {
        throw UnsupportedOperationException()
    }
    override fun getRequestedSessionId() : String? {
        throw UnsupportedOperationException()
    }
    override fun getLocalPort() : Int {
        throw UnsupportedOperationException()
    }
    override fun getRequestDispatcher(p0 : String?) : RequestDispatcher? {
        throw UnsupportedOperationException()
    }
    override fun getAttribute(p0 : String?) : Any? {
        return null
    }
    override fun getParameter(p0 : String?) : String? {
        return params[p0]
    }
    override fun getRemoteHost() : String? {
        throw UnsupportedOperationException()
    }
    override fun getAttributeNames() : Enumeration<String>? {
        throw UnsupportedOperationException()
    }

    override fun getParameterNames() : Enumeration<String>? {
        return params.keys()
    }
    override fun setAttribute(p0 : String?, p1 : Any?) {
        throw UnsupportedOperationException()
    }
    override fun setCharacterEncoding(p0 : String?) {
        throw UnsupportedOperationException()
    }
    override fun isSecure() : Boolean {
        throw UnsupportedOperationException()
    }
    override fun getScheme() : String? {
        throw UnsupportedOperationException()
    }
    override fun getLocalName() : String? {
        throw UnsupportedOperationException()
    }
    override fun getCharacterEncoding() : String? {
        throw UnsupportedOperationException()
    }
    override fun getServerName() : String? {
        throw UnsupportedOperationException()
    }
    override fun getProtocol() : String? {
        throw UnsupportedOperationException()
    }
    override fun getContentLength() : Int {
        throw UnsupportedOperationException()
    }
    override fun getRealPath(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    override fun getParameterMap() : MutableMap<String, Array<String>?>? {
        return params.map { it.key to arrayOf(it.value) }.toMap(LinkedHashMap())
    }
    override fun getInputStream() : ServletInputStream? {
        throw UnsupportedOperationException()
    }
    override fun getLocalAddr() : String? {
        throw UnsupportedOperationException()
    }
    override fun removeAttribute(p0 : String?) {
        throw UnsupportedOperationException()
    }
    override fun getServerPort() : Int {
        throw UnsupportedOperationException()
    }
    override fun getRemoteAddr() : String? {
        throw UnsupportedOperationException()
    }
    override fun getLocales() : Enumeration<Locale>? {
        throw UnsupportedOperationException()
    }
    override fun getLocale() : Locale? {
        throw UnsupportedOperationException()
    }
    override fun getContentType() : String? {
        return "text/html"
    }
    override fun getRemotePort() : Int {
        throw UnsupportedOperationException()
    }
    override fun getParameterValues(p0 : String?) : Array<String>? {
        return params[p0]?.split(RECORD_SEPARATOR_CHAR)?.toTypedArray()
    }
    override fun getReader() : BufferedReader? {
        throw UnsupportedOperationException()
    }

    override fun getContentLengthLong(): Long {
        throw UnsupportedOperationException()
    }

    override fun <T : HttpUpgradeHandler?> upgrade(p0: Class<T>?): T? {
        throw UnsupportedOperationException()
    }

    override fun changeSessionId(): String? {
        throw UnsupportedOperationException()
    }
}
