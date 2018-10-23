package com.dragbone.dg_fy.server.user

import java.io.IOException
import java.util.concurrent.TimeUnit

class UserIdentifier {
    fun getUser(ip: String, timeoutMs: Long = 500): String? {
        return try {
            val proc = ProcessBuilder("query", "user", "/server:$ip")
                    .start()
            proc.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
            parseName(proc.inputStream.bufferedReader().readText())
        } catch (e: IOException) {
            println(e)
            null
        }
    }

    private fun parseName(text: String): String {
        try {
            val data = text.lines()[1].trim().split(Regex("""\s+"""))
            return data[0]
        } catch (e: Exception) {
            throw IOException("Couldn't parse: $text", e)
        }
    }
}