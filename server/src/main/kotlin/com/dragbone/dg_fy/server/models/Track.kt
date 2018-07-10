package com.dragbone.dg_fy.server.models

import java.util.*

open class Track(val trackId: String) {
    val queueDate = Date()
    var numVotes: Int = 0
    var artist: String? = null
    var song: String? = null
    var imageUrl: String? = null
    var lengthS: Int? = null
    val userVotes = mutableMapOf<String, VoteTypes>()
}