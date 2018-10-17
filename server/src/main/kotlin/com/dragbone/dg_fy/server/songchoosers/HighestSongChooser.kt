package com.dragbone.dg_fy.server.songchoosers

import com.dragbone.dg_fy.server.models.Track

class HighestSongChooser : SongChooser {
    override fun selectSong(songs: Collection<Track>): String? {
        return songs.maxBy { it.numVotes }?.trackId
    }
}