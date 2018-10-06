package com.dragbone.dg_fy.server

import spark.kotlin.RouteHandler
import spark.kotlin.halt
import java.util.*

class AdminFilter(private val adminPassword: String) {
    fun check(routeHandler: RouteHandler) {
        val authHeader = routeHandler.request.headers("Authorization") ?: return halty()
        val base64Login = authHeader.removePrefix("Basic").trim()
        val login = try {
            String(Base64.getDecoder().decode(base64Login))
        } catch (e: Exception) {
            return halty()
        }
        val userPassword = login.substringAfter(":")
        if (adminPassword != userPassword) {
            return halty()
        }
    }

    private fun halty() {
        halt(403, "Credentials required")
    }
}