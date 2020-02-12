package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.lib.AppCommand
import com.dragbone.dg_fy.server.config.BoolConfigEntry
import com.dragbone.dg_fy.server.config.Configs
import com.dragbone.dg_fy.server.config.IntConfigEntry
import com.dragbone.dg_fy.server.models.Error
import com.dragbone.dg_fy.server.models.StateDataSet
import com.dragbone.dg_fy.server.user.UserMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Response
import spark.kotlin.*
import java.net.URLEncoder

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Start server with 'java -jar server.jar <clientId> <clientSecret> <password>'")
    }
    val adminFilter = AdminFilter(args[2])
    val http: Http = ignite()
    val spotifyClient = SpotifyClient(args[0], args[1])
    val playlistManager = PlaylistManager(spotifyClient)
    val config = mutableMapOf(
            Configs.Vote to BoolConfigEntry(true),
            Configs.MuteDuration to IntConfigEntry(5))
    val muteService = MuteService(config)
    val commandQueue = mutableListOf<AppCommand>()
    val userMapper = UserMapper()

    http.port(5080)
    http.staticFiles.externalLocation("webapp/build")

    http.enableCORS("*", "*", "*")

    http.setupConfigRoutes(config, adminFilter)
    http.setupQueueRoutes(playlistManager, config, muteService, commandQueue, adminFilter, userMapper)
    http.setupAdminRoutes(playlistManager, muteService, commandQueue, adminFilter)

    http.post("/api/login") {
        adminFilter.check(this)
        "true".json()
    }

    http.get("/api/search/:q") {
        val searchRaw = params("q")
        val search = URLEncoder.encode(searchRaw, "UTF-8")
        println("Searching for: $search")
        spotifyClient.search(search)
    }

    http.get("/api/progress/:progressS") {
        val progress = params("progressS").toInt()
        playlistManager.progress = progress

        if (muteService.isMuteExpired()) {
            muteService.resetMute()
            commandQueue.add(AppCommand.Play)
        }

        if (commandQueue.any()) {
            commandQueue.removeAt(0)
        } else {
            AppCommand.Nop
        }
    }

    http.get("/api/mute") {
        muteService.getDataSet().json()
    }
    http.post("/api/mute") {
        commandQueue.add(AppCommand.Pause)
        val user = userMapper.getUser(request.ip()) ?: return@post Error("Anonymous muting is not allowed").json()
        muteService.increaseMute(user)
        StateDataSet(playlistManager.getPlaylist(request.ip()), muteService.getDataSet()).json()
    }
}

val mapper = jacksonObjectMapper()
fun Any.json(): String = mapper.writeValueAsString(this)

fun Response.error(message: String): String {
    status(400)
    return Error(message).json()
}

fun Http.enableCORS(origin: String, methods: String, headers: String) {
    options("/*") {
        val accessControlRequestHeaders = request.headers("Access-Control-Request-Headers")
        if (accessControlRequestHeaders != null) {
            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders)
        }

        val accessControlRequestMethod = request.headers("Access-Control-Request-Method")
        if (accessControlRequestMethod != null) {
            response.header("Access-Control-Allow-Methods", accessControlRequestMethod)
        }

        "OK"
    }
    before {
        response.header("Access-Control-Allow-Origin", origin)
        response.header("Access-Control-Request-Method", methods)
        response.header("Access-Control-Allow-Headers", headers)
        response.header("Access-Control-Allow-Credentials", "true")
        // Note: this may or may not be necessary in your particular application
        response.type("application/json")
    }
}