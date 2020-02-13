package com.dragbone.dg_fy.server.songchoosers

import com.dragbone.dg_fy.server.models.Track
import java.lang.Exception
import java.util.*

class LikelihoodSongChooser : SongChooser {
    private val random: Random = Random()
    override fun selectSong(songs: Collection<Track>): String? {
        val possibleSongs = songs.filter { it.numVotes >= 0 }
        val totalVotes = possibleSongs.sumBy { it.numVotes }
        val totalVoteChoices = totalVotes + possibleSongs.size

        if (possibleSongs.none()) return null

        var value = random.nextInt(totalVoteChoices)
        possibleSongs.forEach {
            value -= it.numVotes + 1
            if (value <= 0) {
                return it.trackId
            }
        }
        return null
    }
}