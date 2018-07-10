package com.dragbone.dg_fy.server

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MuteService {
    private var muteUntil: Instant? = null

    fun increaseMute(duration: Duration) {
        val mute = muteUntil ?: Instant.now()
        muteUntil = mute.plus(duration)
    }

    fun getMuteEndDate(): LocalDateTime? = muteUntil?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) }

    fun isMuteExpired(): Boolean {
        return muteUntil?.isBefore(Instant.now()) ?: false
    }

    fun resetMute() {
        muteUntil = null
    }
}