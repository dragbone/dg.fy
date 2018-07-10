package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.lib.AppCommand
import com.dragbone.dg_fy.server.models.VoteTypes
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Request
import spark.kotlin.*
import java.net.URLEncoder
import java.time.Duration

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Start server with 'java -jar server.jar <clientId> <clientSecret> <password>'")
    }
    val adminPassword = args[2]
    val http: Http = ignite()
    val spotifyClient = SpotifyClient(args[0], args[1])
    val playlistManager = PlaylistManager(spotifyClient)
    val config = mutableMapOf(Configs.Vote to true)

    http.port(80)
    http.staticFiles.externalLocation("webapp/build")

    http.enableCORS("*", "*", "*")

    http.get("/api/login") {
        val password = request.queryParams("password")
        println("Login: $password")
        response.cookie("password", password, 60 * 60 * 24 * 30 * 6, false, false)
        password == adminPassword
    }

    http.get("/api/isLoggedIn") {
        checkPassword(request, adminPassword)
    }

    http.get("/api/search/:q") {
        val searchRaw = params("q")
        val search = URLEncoder.encode(searchRaw, "UTF-8")
        println("Searching for: $search")
        spotifyClient.search(search)
    }

    val commandQueue = mutableListOf<AppCommand>()
    http.get("/api/progress/:progressS") {
        val progress = params("progressS").toInt()
        playlistManager.progress = progress

        if (playlistManager.muteService.isMuteExpired()) {
            playlistManager.muteService.resetMute()
            commandQueue.add(AppCommand.Play)
        }

        if (commandQueue.any()) {
            commandQueue.removeAt(0)
        } else {
            AppCommand.Nop
        }
    }

    http.get("/api/queue") {
        if (playlistManager.muteService.isMuteExpired()) {
            playlistManager.muteService.resetMute()
            commandQueue.add(AppCommand.Play)
        }
        playlistManager.getPlaylist(request.ip()).json()
    }

    http.get("/api/config/:param/:value") {
        if (!checkPassword(request, adminPassword)) return@get false
        val param = Configs.valueOf(params("param").capitalize())
        val value = params("value").toBoolean()
        config[param] = value
        "$param=$value"
    }
    http.get("/api/skip") {
        if (!checkPassword(request, adminPassword)) return@get false
        commandQueue.add(AppCommand.Skip)
    }
    http.get("/api/pause") {
        if (!checkPassword(request, adminPassword)) return@get false
        commandQueue.add(AppCommand.Pause)
    }
    http.get("/api/play") {
        if (!checkPassword(request, adminPassword)) return@get false
        commandQueue.add(AppCommand.Play)
        playlistManager.muteService.resetMute()
    }
    http.get("/api/mute") {
        commandQueue.add(AppCommand.Pause)
        playlistManager.muteService.increaseMute(Duration.ofMinutes(5))
        playlistManager.getPlaylist(request.ip()).json()
    }

    http.get("/api/queue/next") {
        playlistManager.dequeue()
    }

    http.get("/api/queue/add/:trackId") {
        if (!config[Configs.Vote]!!) return@get "Voting is disabled"
        var voteType = VoteTypes.NONE
        if (request.queryParams("voteType")?.toLowerCase() == "downvote") {
            voteType = VoteTypes.DOWNVOTE
        } else if (request.queryParams("voteType")?.toLowerCase() == "upvote") {
            voteType = VoteTypes.UPVOTE
        }
        val track = playlistManager.add(params("trackId"), request.ip(), voteType)
        track.json()
    }

    http.get("/api/queue/remove/:trackId") {
        val track = playlistManager.remove(params("trackId"), request.ip())
        track?.json() ?: ""
    }
}

fun checkPassword(request: Request, adminPassword: String): Boolean {
    val userPassword = request.cookie("password")
    return adminPassword == userPassword
}

val mapper = jacksonObjectMapper()
fun Any.json(): String = mapper.writeValueAsString(this)

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