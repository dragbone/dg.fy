package com.dragbone.dg_fy

import android.os.AsyncTask
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import com.spotify.sdk.android.player.Error
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerEvent
import java.net.URL
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class SpotifyPlaylistPlayer(val player: Player, val textView: TextView, val progressBar: ProgressBar)
    : Player.NotificationCallback {
    companion object {
        val initialTrack = "5TQbdFgOgAMwhAzZwVFBHb"
    }

    val timer = Timer("dg.fy")

    init {
        timer.scheduleAtFixedRate(0, 1000) {
            try {
                val posMS = player.playbackState?.positionMs ?: 0
                progressBar.progress = (posMS / 1000).toInt()
                request("progress/" + progressBar.progress)
            } catch (exception: Exception) {
                Log.e("TimerException", exception.toString())
            }
        }
    }

    override fun onPlaybackError(error: Error) {
        Log.e("SpotifyPlaylistPlayer", error.toString())
    }

    override fun onPlaybackEvent(event: PlayerEvent) {
        Log.d("SpotifyPlaylistPlayer", event.name)
        when (event) {
            PlayerEvent.kSpPlaybackNotifyBecameActive, PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone -> run {
                PlayNextSongTask().execute(player)
            }
            PlayerEvent.kSpPlaybackNotifyTrackChanged -> run {
                val track = player.metadata.currentTrack
                textView.text = "${track.artistName} - ${track.name}"
                progressBar.max = (track.durationMs / 1000).toInt()
            }
        }
    }

    fun request(req: String): String {
        val url = "http://" + MainActivity.host + "/api/$req"
        return URL(url).openConnection().getInputStream().use {
            it.bufferedReader().readLine()
        }
    }

    inner class PlayNextSongTask : AsyncTask<Player, Unit, Unit>() {
        override fun doInBackground(vararg params: Player) {
            val player = params[0]
            val track = getNextTrack()
            Log.d("SpotifyPlaylistPlayer", "dequeued track $track")
            player.playUri(null, "spotify:track:$track", 0, 0)
        }

        fun getNextTrack(): String {
            val track = request("queue/next")
            return track
        }
    }

    fun init() {
        player.playUri(null, "spotify:track:$initialTrack", 0, 0)
    }

    private val playlist = mutableListOf<String>()
    fun queue(track: String) {
        playlist.add(track)
    }
}