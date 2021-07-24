package com.example.ktor

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.ktor.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureRouting()
        configureSecurity()
        configureMonitoring()
        configureSerialization()
        configureSockets()
        configureAdministration()
        configureTemplating()
    }.start(wait = true)
}
