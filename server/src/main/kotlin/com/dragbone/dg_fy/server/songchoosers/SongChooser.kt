package com.dragbone.dg_fy.server.songchoosers

import com.dragbone.dg_fy.server.models.Track

interface SongChooser {
    fun selectSong(songs: Collection<Track>): String?
}