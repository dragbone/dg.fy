package com.dragbone.dg_fy.server.models

class UserTrack(track: Track, val voteType: VoteTypes) : Track(track.trackId) {
    init {
        numVotes = track.numVotes
        artist = track.artist
        song = track.song
        imageUrl = track.imageUrl
        userVotes.putAll(track.userVotes)
    }
}