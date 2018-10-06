package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.server.config.Configs
import com.dragbone.dg_fy.server.config.IConfigEntry
import com.dragbone.dg_fy.server.models.Error
import spark.kotlin.Http

fun Http.setupConfigRoutes(config: MutableMap<Configs, IConfigEntry>, adminFilter: AdminFilter) {
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
        adminFilter.check(this)
        val param = params("param")
        val configType = Configs.fromStringOrNull(param)
                ?: return@post response.error("Unknown parameter '$param'")

        val value = params("value")
        println("config-change: $param = $value")
        config[configType]!!.setValue(value)
        config.json()
    }
}