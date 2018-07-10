package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.server.models.MuteInfo
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MuteService {
    private var muteUntil: Instant? = null

    val increaseDurationMinutes = 5
    fun increaseMute() {
        val mute = muteUntil ?: Instant.now()
        muteUntil = mute.plus(Duration.ofMinutes(increaseDurationMinutes.toLong()))
    }

    fun getMuteEndDate(): LocalDateTime? = muteUntil?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) }

    fun isMuteExpired(): Boolean {
        return muteUntil?.isBefore(Instant.now()) ?: false
    }

    fun resetMute() {
        muteUntil = null
    }

    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    fun getDataSet(): MuteInfo {
        return MuteInfo(getMuteEndDate()?.format(formatter), increaseDurationMinutes)
    }
}