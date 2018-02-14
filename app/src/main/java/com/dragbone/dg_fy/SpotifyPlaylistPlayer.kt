package com.dragbone.dg_fy

import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.dragbone.dg_fy.lib.AppCommand
import com.spotify.sdk.android.player.Error
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerEvent
import java.net.URL
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class SpotifyPlaylistPlayer(val player: Player, val textView: TextView, val progressBar: ProgressBar, val imageView: ImageView)
    : Player.NotificationCallback {
    companion object {
        val initialTrack = "5TQbdFgOgAMwhAzZwVFBHb"
    }

    val timer = Timer("dg.fy")
    var command = AppCommand.Nop

    init {
        timer.scheduleAtFixedRate(0, 200) {
            try {
                when (command) {
                    AppCommand.Play -> if (player.playbackState == null) {
                        PlayNextSongTask().execute(player)
                    } else if (!player.playbackState.isPlaying) {
                        player.resume(LoggingOperationCallback("Resume"))
                    }
                    AppCommand.Pause -> if (player.playbackState?.isPlaying ?: false) {
                        player.pause(LoggingOperationCallback("Pause"))
                    }
                    AppCommand.Skip -> run {
                        command = AppCommand.Play
                        PlayNextSongTask().execute(player)
                    }
                }

                val posMS = player.playbackState?.positionMs ?: 0
                progressBar.progress = (posMS / 1000).toInt()
                val response = request("progress/" + progressBar.progress)
                val newCommand = AppCommand.valueOf(response)
                if (newCommand != AppCommand.Nop) {
                    command = newCommand
                }
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
                val track = player.metadata.currentTrack ?: return
                textView.text = "${track.artistName} - ${track.name}"
                progressBar.max = (track.durationMs / 1000).toInt()
                Log.w("ImageUri", track.albumCoverWebUrl)
                ImageDownloadTask(imageView, URL(track.albumCoverWebUrl)).execute()
            }
        }
    }

    fun request(req: String): String {
        val url = "http://" + MainActivity.host + "/api/$req"
        val connection = URL(url).openConnection()
        connection.connectTimeout = 5000
        return connection.getInputStream().use {
            it.bufferedReader().readLine()
        }
    }

    inner class PlayNextSongTask : AsyncTask<Player, Unit, Unit>() {
        override fun doInBackground(vararg params: Player) {
            try {
                val player = params[0]
                val track = try {
                    getNextTrack()
                } catch (e: Exception) {
                    Log.e("PlayNextSongTask", "Could not retrieve next track: $e")
                    initialTrack
                }
                Log.d("PlayNextSongTask", "playing next track: $track")
                player.playUri(LoggingOperationCallback("PlayUri"), "spotify:track:$track", 0, 0)
            } catch (e: Exception) {
                Log.e("PlayNextSongTask", e.toString())
            }
        }

        fun getNextTrack(): String {
            val track = request("queue/next")
            return track
        }
    }

    fun init() {
        player.playUri(LoggingOperationCallback("PlayUri"), "spotify:track:$initialTrack", 0, 0)
    }
}