package kara

import kara.internal.ResourceDescriptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class Interceptor(val priority: Int) : Comparable<Interceptor> {
    operator abstract fun invoke(request: HttpServletRequest, response: HttpServletResponse, descriptor: ResourceDescriptor?,
               proceed: (HttpServletRequest, HttpServletResponse, ResourceDescriptor?) -> Boolean): Boolean

    override fun compareTo(other: Interceptor): Int {
        return this.priority - other.priority
    }
}