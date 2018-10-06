package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.lib.AppCommand
import com.dragbone.dg_fy.server.models.StateDataSet
import com.dragbone.dg_fy.server.models.VoteTypes
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Request
import spark.kotlin.*
import java.lang.Exception
import java.net.URLEncoder
import java.util.*

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Start server with 'java -jar server.jar <clientId> <clientSecret> <password>'")
    }
    val adminPassword = args[2]
    val http: Http = ignite()
    val spotifyClient = SpotifyClient(args[0], args[1])
    val playlistManager = PlaylistManager(spotifyClient)
    val config = mutableMapOf(
            Configs.Vote to BoolConfigEntry(true),
            Configs.MuteDuration to IntConfigEntry(5))
    val muteService = MuteService(config)

    http.port(80)
    http.staticFiles.externalLocation("webapp/build")

    http.enableCORS("*", "*", "*")

    http.setupConfigRoutes(config, adminPassword)

    http.post("/api/login") {
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

    http.get("/api/queue") {
        if (muteService.isMuteExpired()) {
            muteService.resetMute()
            commandQueue.add(AppCommand.Play)
        }
        StateDataSet(playlistManager.getPlaylist(request.ip()), muteService.getDataSet()).json()
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
        muteService.resetMute()
    }
    http.get("/api/mute") {
        muteService.getDataSet().json()
    }
    http.post("/api/mute") {
        commandQueue.add(AppCommand.Pause)
        muteService.increaseMute()
        StateDataSet(playlistManager.getPlaylist(request.ip()), muteService.getDataSet()).json()
    }

    http.get("/api/queue/next") {
        playlistManager.dequeue()
    }

    http.get("/api/queue/add/:trackId") {
        if (!(config[Configs.Vote] as BoolConfigEntry).value) return@get "Voting is disabled"
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
    val authHeader = request.headers("Authorization") ?: return false
    val base64Login = authHeader.removePrefix("Basic").trim()
    val login = try {
        String(Base64.getDecoder().decode(base64Login))
    } catch (e: Exception) {
        return false
    }
    val userPassword = login.substringAfter(":")
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