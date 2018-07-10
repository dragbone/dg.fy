package com.dragbone.dg_fy.server.models

import java.time.LocalDateTime

data class Playlist(val tracks: List<UserTrack>, val playing: PlayingTrack, val muteUntil: String?)