package com.dragbone.dg_fy.lib

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request

class JsonApiClient(val url: String) {
    val httpClient = OkHttpClient()
    val mapper = jacksonObjectMapper()
    val request = Request.Builder()

    fun header(name: String, value: String): JsonApiClient {
        request.addHeader(name, value)
        return this
    }

    inline fun <reified TResponse : Any> requestObject(): TResponse {
        val jsonString = httpClient.newCall(request.build()).execute().body()!!.string()
        return mapper.readValue(jsonString, TResponse::class.java)
    }

    fun requestJson(): JsonNode {
        val jsonString = httpClient.newCall(request.build()).execute().body()!!.string()
        return mapper.readTree(jsonString)
    }
}