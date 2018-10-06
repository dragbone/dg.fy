package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.server.models.Track

class FakeSpotifyClient : ISpotifyClient {
    override fun loadTrackData(track: Track) {
    }

    override fun getRecommendedTrackIds(trackIds: List<String>): List<String> {
        return emptyList()
    }

    override fun search(text: String): String {
        return ""
    }
}