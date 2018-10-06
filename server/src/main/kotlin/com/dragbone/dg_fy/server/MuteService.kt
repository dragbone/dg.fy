package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.server.models.MuteInfo
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MuteService(private val config: MutableMap<Configs, IConfigEntry>) {
    private var muteUntil: Instant? = null
    private val increaseDurationMinutes: Int
        get() {
            return (config[Configs.MuteDuration] as IntConfigEntry).value
        }

    fun increaseMute() {
        val mute = muteUntil ?: Instant.now()
        muteUntil = mute.plus(Duration.ofMinutes(increaseDurationMinutes.toLong()))
    }

    private fun getMuteEndDate(): LocalDateTime? = muteUntil?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) }

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