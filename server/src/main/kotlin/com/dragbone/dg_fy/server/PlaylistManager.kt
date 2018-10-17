package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.server.models.*
import com.dragbone.dg_fy.server.songchoosers.LikelihoodSongChooser
import com.dragbone.dg_fy.server.songchoosers.SongChooser
import java.io.File
import java.lang.Exception
import java.time.format.DateTimeFormatter
import java.util.*

class PlaylistManager(val spotifyClient: ISpotifyClient) {
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
    fun getPlaylist(user: String): Playlist {
        val list = playlist.values.sortedWith(comparator).map {
            UserTrack(it, it.userVotes[user]
                    ?: VoteTypes.NONE)
        }
        return Playlist(list, PlayingTrack(currentlyPlaying, progress))
    }

    private val playlist = mutableMapOf<String, Track>()

    fun add(trackId: String, user: String?, voteType: VoteTypes): UserTrack {
        println("add: $trackId, user: $user")
        val isNewTrack = !playlist.containsKey(trackId)
        if (blackList.contains(trackId) && isNewTrack) return UserTrack(currentlyPlaying!!, VoteTypes.NONE)
        if (currentlyPlaying?.trackId == trackId) return UserTrack(currentlyPlaying!!, VoteTypes.NONE)
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
    private val songChooser: SongChooser = LikelihoodSongChooser()
    fun dequeue(): String {
        val nextTrackId = songChooser.selectSong(playlist.values) ?: return getFallbackTrackId()
        val nextTrack = playlist[nextTrackId] ?: return getFallbackTrackId()
        playedTracks.add(nextTrack)
        playlist.remove(nextTrackId)

        if (playlist.count { it.value.numVotes >= 0 } < 3) {
            refillPlaylist()
        }

        currentlyPlaying = nextTrack
        println("playing: $nextTrackId")
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

    fun purge() {
        playedTracks.clear()
        playlist.clear()
        blackList.clear()
    }

    private val blackList: MutableSet<String> = mutableSetOf()
    fun blacklist(trackId: String) {
        playlist.remove(trackId)
        blackList.add(trackId)
    }

    private val badUserList: MutableMap<String, Int> = mutableMapOf()
    fun addBadUser(user: String) {
        badUserList[user] = badUserList.getOrElse(user) { 0 } + 1
        try {
            File("badUsers.json").writeText(badUserList.json())
        } catch (e: Exception) {
            println("Could not write bad users to file: " + badUserList.json())
        }
    }
}