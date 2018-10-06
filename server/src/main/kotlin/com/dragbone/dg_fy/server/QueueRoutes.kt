package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.lib.AppCommand
import com.dragbone.dg_fy.server.config.BoolConfigEntry
import com.dragbone.dg_fy.server.config.Configs
import com.dragbone.dg_fy.server.config.IConfigEntry
import com.dragbone.dg_fy.server.models.Error
import com.dragbone.dg_fy.server.models.StateDataSet
import com.dragbone.dg_fy.server.models.VoteTypes
import spark.kotlin.Http

fun Http.setupQueueRoutes(playlistManager: PlaylistManager,
                          config: MutableMap<Configs, IConfigEntry>,
                          muteService: MuteService,
                          commandQueue: MutableList<AppCommand>,
                          adminFilter: AdminFilter) {
    get("/api/queue/next") {
        playlistManager.dequeue()
    }

    post("/api/queue/:trackId") {
        if (!(config[Configs.Vote] as BoolConfigEntry).value) return@post Error("Voting is disabled").json()
        var voteType = VoteTypes.NONE
        if (request.queryParams("voteType")?.toLowerCase() == "downvote") {
            voteType = VoteTypes.DOWNVOTE
        } else if (request.queryParams("voteType")?.toLowerCase() == "upvote") {
            voteType = VoteTypes.UPVOTE
        }
        val track = playlistManager.add(params("trackId"), request.ip(), voteType)
        track.json()
    }

    delete("/api/queue/:trackId") {
        val track = playlistManager.remove(params("trackId"), request.ip())
        track?.json() ?: ""
    }

    get("/api/queue") {
        if (muteService.isMuteExpired()) {
            muteService.resetMute()
            commandQueue.add(AppCommand.Play)
        }
        StateDataSet(playlistManager.getPlaylist(request.ip()), muteService.getDataSet()).json()
    }
}