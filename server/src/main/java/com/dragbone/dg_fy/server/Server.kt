package com.dragbone.dg_fy.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.kotlin.*

fun main(args: Array<String>) {
    if(args.size != 2){
        println("Start server with 'java -jar server.jar <clientId> <clientSecret>'")
    }
    val http: Http = ignite()
    val spotifyClient = SpotifyClient(args[0], args[1])
    val playlistManager = PlaylistManager(spotifyClient)

    http.enableCORS("*", "*", "*")

    http.get("/api/search/:q") {
        println("Searching for: ${params("q")}")
        spotifyClient.search(params("q"))
    }

    http.get("/api/progress/:progressS") {
        val progress = params("progressS").toInt()
        playlistManager.progress = progress
        progress
    }

    http.get("/api/queue") {
        playlistManager.getPlaylist(request.ip()).json()
    }

    http.get("/api/queue/next") {
        playlistManager.dequeue()
    }

    http.get("/api/queue/add/:trackId") {
        val track = playlistManager.add(params("trackId"), request.ip())
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