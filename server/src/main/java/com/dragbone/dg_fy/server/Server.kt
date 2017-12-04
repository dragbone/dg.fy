package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.lib.AppCommand
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.kotlin.*
import java.net.URLEncoder

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Start server with 'java -jar server.jar <clientId> <clientSecret> <password>'")
    }
    val password = args[2]
    val http: Http = ignite()
    val spotifyClient = SpotifyClient(args[0], args[1])
    val playlistManager = PlaylistManager(spotifyClient)

    http.enableCORS("*", "*", "*")

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
        if (commandQueue.any()) {
            commandQueue.removeAt(0)
        } else {
            AppCommand.Nop
        }
    }

    http.get("/api/queue") {
        playlistManager.getPlaylist(request.ip()).json()
    }

    http.get("/api/skip") {
        if (request.queryParams("password") != password) return@get false
        commandQueue.add(AppCommand.Skip)
    }
    http.get("/api/pause") {
        if (request.queryParams("password") != password) return@get false
        commandQueue.add(AppCommand.Pause)
    }
    http.get("/api/play") {
        if (request.queryParams("password") != password) return@get false
        commandQueue.add(AppCommand.Play)
    }

    http.get("/api/queue/next") {
        playlistManager.dequeue()
    }

    http.get("/api/queue/add/:trackId") {
        var voteType = VoteTypes.NONE
        if(request.queryParams("voteType")?.toLowerCase() == "downvote"){
            voteType = VoteTypes.DOWNVOTE
        } else if(request.queryParams("voteType")?.toLowerCase() == "upvote"){
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
        // Note: this may or may not be necessary in your particular application
        response.type("application/json")
    }
}