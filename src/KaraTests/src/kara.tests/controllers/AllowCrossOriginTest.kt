package kara.tests.controllers

import kara.*
import kara.internal.ResourceDescriptor.Companion.ALLOW_CROSS_ORIGIN_HEADER
import kara.tests.mock.mockDispatch
import org.junit.Test
import javax.servlet.http.HttpServletResponse
import kotlin.test.assertEquals

@Location("/cross")
object CrossOriginRoutes {
    @Post("/no-origin")
    object NoCrossOrigin : Request({ TextResult("") })

    @Post("/all-allowed", allowCrossOrigin = "*")
    object AllAllowedCrossOrigin : Request({ TextResult("") })

    @Post("/full-single-origin", allowCrossOrigin = "http://www.test.com")
    object FullQualifiedSingleOrigin : Request({ TextResult("") })

    @Post("/secure-single-origin", allowCrossOrigin = "https://www.test.com")
    object FullQualifiedSingleSecureOrigin : Request({ TextResult("") })

    @Post("/secure-multiple-origin", allowCrossOrigin = "http://www.test.com http://www.test.me https://www.test.com")
    object FullQualifiedMultipleOrigins : Request({ TextResult("") })


    @Post("/domain-short-single", allowCrossOrigin = "test.com")
    object DomainOnlyShortSingleOrigin : Request({ TextResult("") })

    @Post("/sub-domain-short-single", allowCrossOrigin = "subdomain.test.com")
    object SubDomainOnlyShortSingleOrigin : Request({ TextResult("") })

    @Post("/domain-short-multiple", allowCrossOrigin = "test.com test.me")
    object DomainOnlyShortMultipleOrigin : Request({ TextResult("") })
}

class AllowCrossOriginTest {


    private fun assertURL(url: String, origin: String, expected: String) {
        val response = mockDispatch("POST", url) {
            addHeader("Origin", origin)
        }

        assertEquals(expected, response.getHeader(ALLOW_CROSS_ORIGIN_HEADER).orEmpty())
    }


    private fun assertRoute(route: Request, origin: String, expected: String) {
        assertURL(route::class.baseLink().href(), origin, expected)
    }
    
    private fun assertRouteError(route: Request, origin: String) {
        val status = mockDispatch("POST", route::class.baseLink().href()) {
            addHeader("Origin", origin)
        }.status
        assertEquals(HttpServletResponse.SC_FORBIDDEN, status)
    }

    private val emptyOrigin = ""
    private val testComOrigin = "http://www.test.com"
    private val subdomainTestComOrigin = "https://www.subdomain.test.com"
    private val wrongSubdomainTestComOrigin = "https://Asubdomain.test.com"
    private val testComSecureOrigin = "https://www.test.com"
    private val testMeOrigin = "http://www.test.me"
    private val testOrgOrigin = "http://www.test.org"

    @Test
    fun test() {
        assertURL("/cross/no-origin", emptyOrigin, "")
        assertURL("/cross/no-origin", testComOrigin, "")
        assertURL("/cross/no-origin", subdomainTestComOrigin, "")
        assertURL("/cross/no-origin", testComSecureOrigin, "")
        assertURL("/cross/no-origin", testMeOrigin, "")
        assertURL("/cross/no-origin", testOrgOrigin, "")

        CrossOriginRoutes.AllAllowedCrossOrigin.let { route ->
            assertRoute(route, emptyOrigin, "*")
            assertRoute(route, testComOrigin, "*")
            assertRoute(route, subdomainTestComOrigin, "*")
            assertRoute(route, testComSecureOrigin, "*")
            assertRoute(route, testMeOrigin, "*")
            assertRoute(route, testOrgOrigin, "*")
        }

        CrossOriginRoutes.FullQualifiedSingleOrigin.let { route ->
            assertRouteError(route, emptyOrigin)
            assertRoute(route, testComOrigin, testComOrigin)
            assertRouteError(route, subdomainTestComOrigin)
            assertRouteError(route, testComSecureOrigin)
            assertRouteError(route, testMeOrigin)
            assertRouteError(route, testOrgOrigin)
        }

        CrossOriginRoutes.FullQualifiedSingleSecureOrigin.let { route ->
            assertRouteError(route, emptyOrigin)
            assertRouteError(route, testComOrigin)
            assertRouteError(route, subdomainTestComOrigin)
            assertRoute(route, testComSecureOrigin, testComSecureOrigin)
            assertRouteError(route, testMeOrigin)
            assertRouteError(route, testOrgOrigin)
        }

        CrossOriginRoutes.FullQualifiedMultipleOrigins.let { route ->
            assertRouteError(route, emptyOrigin)
            assertRoute(route, testComOrigin, testComOrigin)
            assertRouteError(route, subdomainTestComOrigin)
            assertRoute(route, testComSecureOrigin, testComSecureOrigin)
            assertRoute(route, testMeOrigin, testMeOrigin)
            assertRouteError(route, testOrgOrigin)
        }

        CrossOriginRoutes.DomainOnlyShortSingleOrigin.let { route ->
            assertRouteError(route, emptyOrigin)
            assertRoute(route, testComOrigin, testComOrigin)
            assertRoute(route, subdomainTestComOrigin, subdomainTestComOrigin)
            assertRoute(route, testComSecureOrigin, testComSecureOrigin)
            assertRouteError(route, testMeOrigin)
            assertRouteError(route, testOrgOrigin)
        }

        CrossOriginRoutes.DomainOnlyShortSingleOrigin.let { route ->
            assertRouteError(route, emptyOrigin)
            assertRoute(route, testComOrigin, testComOrigin)
            assertRoute(route, subdomainTestComOrigin, subdomainTestComOrigin)
            assertRoute(route, testComSecureOrigin, testComSecureOrigin)
            assertRouteError(route, testMeOrigin)
            assertRouteError(route, testOrgOrigin)
        }

        CrossOriginRoutes.SubDomainOnlyShortSingleOrigin.let { route ->
            assertRouteError(route, emptyOrigin)
            assertRouteError(route, testComOrigin)
            assertRoute(route, subdomainTestComOrigin, subdomainTestComOrigin)
            assertRoute(route, subdomainTestComOrigin.replace("www.", ""), subdomainTestComOrigin.replace("www.", ""))
            assertRouteError(route, wrongSubdomainTestComOrigin)
            assertRouteError(route, testComSecureOrigin)
            assertRouteError(route, testMeOrigin)
            assertRouteError(route, testOrgOrigin)
        }

        CrossOriginRoutes.DomainOnlyShortMultipleOrigin.let { route ->
            assertRouteError(route, emptyOrigin)
            assertRoute(route, testComOrigin, testComOrigin)
            assertRoute(route, subdomainTestComOrigin, subdomainTestComOrigin)
            assertRoute(route, subdomainTestComOrigin.replace("www.", ""), subdomainTestComOrigin.replace("www.", ""))
            assertRoute(route, wrongSubdomainTestComOrigin, wrongSubdomainTestComOrigin)
            assertRoute(route, testComSecureOrigin, testComSecureOrigin)
            assertRoute(route, testMeOrigin, testMeOrigin)
            assertRouteError(route, testOrgOrigin)
        }
    }
}