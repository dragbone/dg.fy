package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.server.models.*
import java.time.format.DateTimeFormatter
import java.util.*

class PlaylistManager(val spotifyClient: ISpotifyClient) {
    val muteService = MuteService()
    companion object {
        const val fallbackTrackId = "5TQbdFgOgAMwhAzZwVFBHb"
        val comparator = kotlin.Comparator<Track> { a, b ->
            val voteDiff = b.numVotes - a.numVotes
            if (voteDiff != 0) voteDiff else (
                    if (a.queueDate.before(b.queueDate)) -1 else 1
                    )
        }
    }

    var progress: Int = 0
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    fun getPlaylist(user: String): Playlist {
        val list = playlist.values.sortedWith(comparator).map {
            UserTrack(it, it.userVotes[user]
                    ?: VoteTypes.NONE)
        }
        return Playlist(list, PlayingTrack(currentlyPlaying, progress), muteService.getMuteEndDate()?.format(formatter))
    }

    private val playlist = mutableMapOf<String, Track>()

    fun add(trackId: String, user: String?, voteType: VoteTypes): UserTrack {
        println("add: $trackId, user: $user")
        if(currentlyPlaying?.trackId == trackId) return UserTrack(currentlyPlaying!!, VoteTypes.NONE);
        val track = playlist.getOrPut(trackId) { Track(trackId) }
        if (user != null) {
            remove(trackId, user)
            track.userVotes[user] = voteType
            if (voteType == VoteTypes.UPVOTE) {
                track.numVotes += 1
            } else if (voteType == VoteTypes.DOWNVOTE) {
                track.numVotes -= 1
            }
        }
        if (track.artist == null) {
            updateTrackData(track)
        }
        return UserTrack(track, voteType)
    }

    fun remove(trackId: String, user: String): UserTrack? {
        println("remove: $trackId, user: $user")
        val track = playlist[trackId] ?: return null
        val voteType = track.userVotes.remove(user)
        if (voteType == VoteTypes.UPVOTE) {
            track.numVotes -= 1
        } else if (voteType == VoteTypes.DOWNVOTE) {
            track.numVotes += 1
        }
        return UserTrack(track, VoteTypes.NONE)
    }

    var currentlyPlaying: Track? = null
    private val playedTracks = mutableListOf<Track>()
    fun dequeue(): String {
        val highestTrack = playlist.entries.maxBy { it.value.numVotes }?.apply {
            playlist.remove(key)
            playedTracks.add(value)
        }

        if (playlist.count { it.value.numVotes >= 0 } < 3) {
            refillPlaylist()
        }

        currentlyPlaying = highestTrack?.value
        val nextTrackId = highestTrack?.key ?: getFallbackTrackId()
        println("dequeue: $nextTrackId")
        return nextTrackId
    }

    private fun updateTrackData(track: Track) {
        spotifyClient.loadTrackData(track)
    }

    private fun refillPlaylist() {
        val trackIds = selectTracks().map { it.trackId }
        val recommendedTrackIds = spotifyClient.getRecommendedTrackIds(trackIds)
        recommendedTrackIds.forEach { add(it, null, VoteTypes.NONE) }
    }

    private fun selectTracks(): List<Track> {
        val weightedTracks = mutableListOf<Track>()
        playedTracks
                .filter { it.numVotes > 0 }
                .takeLast(100)
                .forEach { track -> track.userVotes.forEach { weightedTracks.add(track) } }
        Collections.shuffle(weightedTracks)
        return weightedTracks.take(5)
    }

    private fun getFallbackTrackId(): String {
        return fallbackTrackId
    }
}