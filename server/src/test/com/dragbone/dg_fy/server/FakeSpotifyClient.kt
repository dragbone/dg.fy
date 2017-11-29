package com.dragbone.dg_fy.server

class FakeSpotifyClient : ISpotifyClient {
    override fun getRecommendedTrackIds(trackIds: List<String>): List<String> {
        return emptyList()
    }

    override fun loadTrackData(track: PlaylistManager.Track) {
    }

    override fun search(text: String): String {
        return ""
    }
}