package com.dragbone.dg_fy.server

import java.util.*

enum class VoteTypes{
    UPVOTE, DOWNVOTE, NONE
}

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
        val userVotes = mutableMapOf<String, VoteTypes>()
    }

    class UserTrack(track: Track, val voteType: VoteTypes) : Track(track.trackId) {
        init {
            numVotes = track.numVotes
            artist = track.artist
            song = track.song
            imageUrl = track.imageUrl
            userVotes.putAll(track.userVotes)
        }
    }

    data class PlayingTrack(val track: Track?, val progress: Int)
    data class Playlist(val tracks: List<UserTrack>, val playing: PlayingTrack)

    fun getPlaylist(user: String): Playlist {
        val list = playlist.values.sortedWith(comparator).map {
            PlaylistManager.UserTrack(it, it.userVotes.get(user) ?: VoteTypes.NONE)
        }
        return Playlist(list, PlayingTrack(currentlyPlaying, progress))
    }

    private val playlist = mutableMapOf<String, Track>()

    fun add(trackId: String, user: String?, voteType: VoteTypes): UserTrack {
        println("add: $trackId, user: $user")
        val track = playlist.getOrPut(trackId) { Track(trackId) }
        if(user != null) {
          remove(trackId, user)
          track.userVotes.put(user, voteType)
          if (voteType == VoteTypes.UPVOTE) {
              track.numVotes += 1
          } else if(voteType == VoteTypes.DOWNVOTE){
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
        } else if(voteType == VoteTypes.DOWNVOTE){
            track.numVotes += 1
        }
        return UserTrack(track, VoteTypes.NONE)
    }

    var currentlyPlaying: Track? = null
    val playedTracks = mutableListOf<Track>()
    fun dequeue(): String {
        val highestTrack = playlist.entries.maxBy { it.value.numVotes }?.apply {
            playlist.remove(key)
            playedTracks.add(value)
        }

        if (playlist.size < 3) {
            refillPlaylist()
        }

        currentlyPlaying = highestTrack?.value
        val nextTrackId = highestTrack?.key ?: getFallbackTrackId()
        println("dequeue: $nextTrackId")
        return nextTrackId
    }

    fun updateTrackData(track: Track) {
        spotifyClient.loadTrackData(track)
    }

    private fun refillPlaylist() {
        val trackIds = selectTracks().map { it.trackId }
        val recommendedTrackIds = spotifyClient.getRecommendedTrackIds(trackIds)
        recommendedTrackIds.forEach { add(it, null) }
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

    fun getFallbackTrackId(): String {
        return fallbackTrackId
    }
}