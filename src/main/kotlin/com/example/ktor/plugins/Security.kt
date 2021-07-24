package com.example.ktor.plugins

import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.locations.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    val loginProviders = listOf(
            OAuthServerSettings.OAuth2ServerSettings(
                    name = "github",
                    authorizeUrl = "https://github.com/login/oauth/authorize",
                    accessTokenUrl = "https://github.com/login/oauth/access_token",
                    clientId = "***",
                    clientSecret = "***"
            )
    ).associateBy { it.name }
    authentication {
        oauth("gitHubOAuth") {
            client = HttpClient(Apache)
            providerLookup = { loginProviders[application.locations.resolve<login>(login::class, this).type] }
            urlProvider = { url(login(it.name)) }
        }
    }

    routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
        authenticate("gitHubOAuth") {
            location<login>() {
                param("error") {
                    handle {
                        // TODO: call.loginFailedPage(call.parameters.getAll("error").orEmpty())
                    }
                }
            }

            handle {
                val principal = call.authentication.principal<OAuthAccessTokenResponse>()
                if (principal != null) {
                    // TODO: call.loggedInSuccessResponse(principal)
                } else {
                    // TODO: call.loginPage()
                }
            }
        }

    }
}

data class MySession(val count: Int = 0)
@Location("/login/{type?}")
class login(val type: String = "")
