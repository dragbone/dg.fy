package com.dragbone.dg_fy.server

interface ISpotifyClient {
    fun loadTrackData(track: PlaylistManager.Track)
    fun search(text: String): String
}