package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.lib.AppCommand
import com.dragbone.dg_fy.server.config.BoolConfigEntry
import com.dragbone.dg_fy.server.config.Configs
import com.dragbone.dg_fy.server.config.IConfigEntry
import com.dragbone.dg_fy.server.models.Error
import com.dragbone.dg_fy.server.models.StateDataSet
import com.dragbone.dg_fy.server.models.VoteTypes
import com.dragbone.dg_fy.server.user.UserMapper
import spark.kotlin.Http

fun Http.setupQueueRoutes(playlistManager: PlaylistManager,
                          config: MutableMap<Configs, IConfigEntry>,
                          muteService: MuteService,
                          commandQueue: MutableList<AppCommand>,
                          adminFilter: AdminFilter,
                          userMapper: UserMapper) {
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
        val user = userMapper.getUser(request.ip()) ?: return@post Error("Anonymous voting is not allowed").json()
        val track = playlistManager.add(params("trackId"), user, voteType)
        track.json()
    }

    delete("/api/queue/:trackId") {
        val user = userMapper.getUser(request.ip()) ?: return@delete Error("Anonymous voting is not allowed").json()
        val track = playlistManager.remove(params("trackId"), user)
        track?.json() ?: ""
    }

    get("/api/queue") {
        if (muteService.isMuteExpired()) {
            muteService.resetMute()
            commandQueue.add(AppCommand.Play)
        }
        val user = userMapper.getUser(request.ip())
        StateDataSet(playlistManager.getPlaylist(user), muteService.getDataSet()).json()
    }
}