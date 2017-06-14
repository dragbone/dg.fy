package com.dragbone.dg_fy.server

import org.junit.Assert
import org.junit.Test

class PlaylistManagerComparatorTest {
    @Test
    fun sameTest() {
        val a = PlaylistManager.Track("track1")
        listOf(a, a).sortedWith(PlaylistManager.comparator)
    }

    @Test
    fun sameVotes_earlierFirst() {
        val a = PlaylistManager.Track("track1").apply {
            numVotes = 12
            queueDate.time = 100
        }
        val b = PlaylistManager.Track("track2").apply {
            numVotes = 12
            queueDate.time = 200
        }
        Assert.assertEquals(listOf(a, b).sortedWith(PlaylistManager.comparator).first(), a)
        Assert.assertEquals(listOf(b, a).sortedWith(PlaylistManager.comparator).first(), a)
    }

    @Test
    fun differentVotes_higherFirst() {
        val a = PlaylistManager.Track("track1").apply {
            numVotes = 12
            queueDate.time = 100
        }
        val b = PlaylistManager.Track("track2").apply {
            numVotes = 9
            queueDate.time = 100
        }
        Assert.assertEquals(listOf(a, b).sortedWith(PlaylistManager.comparator).first(), a)
        Assert.assertEquals(listOf(b, a).sortedWith(PlaylistManager.comparator).first(), a)
    }
}