package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.server.models.Track

interface ISpotifyClient {
    fun loadTrackData(track: Track)
    fun search(text: String): String
    fun getRecommendedTrackIds(trackIds: List<String>): List<String>
}