package com.dragbone.dg_fy.server

import java.util.*

class PlaylistManager(val spotifyClient: ISpotifyClient) {
    companion object {
        val fallbackTrackId = "5TQbdFgOgAMwhAzZwVFBHb"
        val comparator = kotlin.Comparator<Track> { a, b ->
            val voteDiff = b.numVotes - a.numVotes
            if (voteDiff != 0) voteDiff else (
                    if (a.queueDate.before(b.queueDate)) -1 else 1
                    )
        }
    }

    var progress: Int = 0

    open class Track(val trackId: String) {
        val queueDate = Date()
        var numVotes: Int = 0
        var artist: String? = null
        var song: String? = null
        var imageUrl: String? = null
        var lengthS: Int? = null
        val userVotes = mutableSetOf<String>()
        val userDownVotes = mutableSetOf<String>()
    }

    class UserTrack(track: Track, val userVote: Boolean, val userDownVote: Boolean) : Track(track.trackId) {
        init {
            numVotes = track.numVotes
            artist = track.artist
            song = track.song
            imageUrl = track.imageUrl
            userVotes.addAll(track.userVotes)
            userDownVotes.addAll(track.userDownVotes)
        }
    }

    data class PlayingTrack(val track: Track?, val progress: Int)
    data class Playlist(val tracks: List<UserTrack>, val playing: PlayingTrack)

    fun getPlaylist(user: String): Playlist {
        val list = playlist.values.sortedWith(comparator).map {
            PlaylistManager.UserTrack(it, it.userVotes.contains(user), it.userDownVotes.contains(user))
        }
        return Playlist(list, PlayingTrack(currentlyPlaying, progress))
    }

    private val playlist = mutableMapOf<String, Track>()

    fun add(trackId: String, user: String): UserTrack {
        println("add: $trackId, user: $user")
        reset(trackId, user)
        val track = playlist.getOrPut(trackId) { Track(trackId) }
        if (!track.userVotes.contains(user)) {
            track.numVotes += 1
            track.userVotes.add(user)
        }
        if (track.artist == null) {
            updateTrackData(track)
        }
        return UserTrack(track, true, false)
    }

    fun remove(trackId: String, user: String): UserTrack? {
        println("remove: $trackId, user: $user")
        reset(trackId, user)
        val track = playlist[trackId] ?: return null
        if (!track.userDownVotes.contains(user)) {
            track.numVotes -= 1
            track.userDownVotes.add(user)
        }
        return UserTrack(track, false, true)
    }

    fun reset(trackId: String, user: String): UserTrack? {
        println("reset: $trackId, user: $user")
        val track = playlist[trackId] ?: return null
        if (track.userVotes.contains(user)) {
            track.numVotes -= 1
            track.userVotes.remove(user)
        }
        if (track.userDownVotes.contains(user)) {
            track.numVotes += 1
            track.userDownVotes.remove(user)
        }
        return UserTrack(track, false, false)
    }

    var currentlyPlaying: Track? = null
    fun dequeue(): String {
        val highestTrack = playlist.maxBy { it.value.numVotes }?.apply {
            playlist.remove(key)
        }
        currentlyPlaying = highestTrack?.value
        val nextTrackId = highestTrack?.key ?: fallbackTrackId
        println("dequeue: $nextTrackId")
        return nextTrackId
    }

    fun updateTrackData(track: Track) {
        spotifyClient.loadTrackData(track)
    }
}