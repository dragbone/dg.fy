package com.dragbone.dg_fy.server

class FakeSpotifyClient :ISpotifyClient{
    override fun loadTrackData(track: PlaylistManager.Track) {
    }

    override fun search(text: String): String {
        return ""
    }
}