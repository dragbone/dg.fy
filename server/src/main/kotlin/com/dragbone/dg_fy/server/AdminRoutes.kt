package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.lib.AppCommand
import com.dragbone.dg_fy.server.models.VoteTypes
import spark.kotlin.Http
import java.io.File
import java.lang.Exception

fun Http.setupAdminRoutes(playlistManager: PlaylistManager,
                          muteService: MuteService,
                          commandQueue: MutableList<AppCommand>,
                          adminFilter: AdminFilter) {

    get("/api/skip") {
        adminFilter.check(this)
        commandQueue.add(AppCommand.Skip)
        try {
            File("skip.txt").appendText(playlistManager.currentlyPlaying?.trackId.toString() + "\n")
        } catch (e: Exception) {
        }
    }

    get("/api/pause") {
        adminFilter.check(this)
        commandQueue.add(AppCommand.Pause)
    }

    get("/api/play") {
        adminFilter.check(this)
        commandQueue.add(AppCommand.Play)
        muteService.resetMute()
    }

    delete("/api/queue") {
        adminFilter.check(this)
        playlistManager.purge()
    }

    get("api/report") {
        adminFilter.check(this)
        playlistManager.currentlyPlaying?.let {
            playlistManager.blacklist(it.trackId)
            it.userVotes.filter { it.value == VoteTypes.UPVOTE }.forEach {
                playlistManager.addBadUser(it.key)
            }
            commandQueue.add(AppCommand.Skip)
            try {
                File("skip.txt").appendText(playlistManager.currentlyPlaying?.trackId.toString() + "\n")
            } catch (e: Exception) {
            }
        }
        ""
    }
}