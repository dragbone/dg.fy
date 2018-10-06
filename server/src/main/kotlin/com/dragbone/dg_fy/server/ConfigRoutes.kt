package com.dragbone.dg_fy.server

import spark.kotlin.Http

fun Http.setupConfigRoutes(config: MutableMap<Configs, IConfigEntry>, adminPassword: String) {
    get("/api/config") {
        config.map { ConfigEntry(name = it.key.name, type = it.value.getHtmlInputType(), value = it.value.getValue()) }.json()
    }

    get("/api/config/:param") {
        val param = params("param")
        val configType = Configs.fromStringOrNull(param)
                ?: return@get Error("Unknown parameter '$param'").json()
        config[configType]!!.json()
    }

    post("/api/config/:param/:value") {
        if (!checkPassword(request, adminPassword)) {
            response.status(403)
            return@post ""
        }
        val param = params("param")
        val configType = Configs.fromStringOrNull(param)
                ?: return@post Error("Unknown parameter '$param'").json()

        val value = params("value")
        println("config-change: $param = $value")
        config[configType]!!.setValue(value)
        config.json()
    }
}

data class Error(val error: String)