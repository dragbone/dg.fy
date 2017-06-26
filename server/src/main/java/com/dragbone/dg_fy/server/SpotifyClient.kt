package com.dragbone.dg_fy.server

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class SpotifyClient(val clientId: String, val clientSecret: String) : ISpotifyClient {
    val httpClient = OkHttpClient()

    var token: String? = null
    val timer = Timer("dg.fy")

    init {
        timer.scheduleAtFixedRate(0, 55L * 60L * 1000L) {
            token = null
        }
    }

    val mapper = jacksonObjectMapper()
    override fun loadTrackData(track: PlaylistManager.Track) {
        val trackData = request("tracks/${track.trackId}")
        with(track) {
            artist = trackData["artists"].first()["name"].asText()
            song = trackData["name"].asText()
            imageUrl = trackData["album"]["images"].last()["url"].asText()
            lengthS = (trackData["duration_ms"].asLong() / 1000).toInt()
        }
    }

    override fun search(text: String): String {
        val searchResult = request("search?q=$text&type=track&market=CH")
        return mapper.writeValueAsString(searchResult)
    }

    private fun token() {
        println("requesting token")
        val url = "https://accounts.spotify.com/api/token"
        val auth = "$clientId:$clientSecret"
        val auth64 = String(Base64.getEncoder().encode(auth.toByteArray()))
        val formBody = FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build()
        val request = Request.Builder()
                .post(formBody)
                .url(url)
                .addHeader("Authorization", "Basic " + auth64)
                .build()
        val json = mapper.readTree(httpClient.newCall(request).execute().body()!!.string())
        token = json["access_token"].asText()
    }

    private fun request(path: String, tryAgain: Boolean = true): JsonNode {
        if (token == null) token()
        val url = "https://api.spotify.com/v1/$path"
        val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer $token")
                .build()
        val jsonString = httpClient.newCall(request).execute().body()!!.string()
        val json = mapper.readTree(jsonString)
        if (json.has("error")) {
            println("error: " + json["error"].toString())
            if (json["error"]["status"].asInt() == 401) {
                println("clearing token...")
                token = null
                if (tryAgain) {
                    println("retrying...")
                    return request(path, tryAgain = false)
                }
            }
        }
        return json
    }
}