package dev.freddiesilver.stocksim.middleware.authentication

import dev.freddiesilver.stocksim.RequestTokenProcessor
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import dev.freddiesilver.stocksim.middleware.resolvers.AuthenticatedUserArgumentResolver
import dev.freddiesilver.stocksim.user.auth.AuthenticatedUser
import kotlin.jvm.java

@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: RequestTokenProcessor,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (handler is HandlerMethod &&
            handler.methodParameters.any {
                it.parameterType == AuthenticatedUser::class.java
            }
        ) {
            var authHeaderValue = request.getHeader(NAME_AUTHORIZATION_HEADER)

            if (authHeaderValue == null && request.cookies != null) {
                val cookie = request.cookies.find { it.name == "token" }

                if (cookie != null) {
                    authHeaderValue = "${RequestTokenProcessor.SCHEME} ${cookie.value}"
                }
            }

            val user = authorizationHeaderProcessor.processAuthorizationHeaderValue(authHeaderValue)

            return if (user == null) {
                response.status = 401
                response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, RequestTokenProcessor.SCHEME)
                false
            } else {
                AuthenticatedUserArgumentResolver.addUserTo(user, request)
                true
            }
        }

        return true
    }

    companion object {
        const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}
