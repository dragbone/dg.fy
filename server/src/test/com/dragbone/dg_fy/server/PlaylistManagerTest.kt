package com.dragbone.dg_fy.server

import com.dragbone.dg_fy.server.models.VoteTypes
import org.junit.Assert
import org.junit.Test

class PlaylistManagerTest {
    @Test
    fun dequeue() {
        /* Arrange */
        val playlistManager = PlaylistManager(FakeSpotifyClient())
        playlistManager.add("track1", "user1", VoteTypes.NONE)
        playlistManager.add("track2", "user1", VoteTypes.NONE)

        /* Act & Assert */
        Assert.assertEquals(playlistManager.dequeue(), "track1")
        Assert.assertEquals(playlistManager.dequeue(), "track2")
    }

    @Test
    fun getPlaylist_empty() {
        /* Arrange */
        val playlistManager = PlaylistManager(FakeSpotifyClient())

        /* Act */
        val playlist = playlistManager.getPlaylist("user1")

        /* Assert */
        Assert.assertEquals(0, playlist.tracks.size)
    }

    @Test
    fun getPlaylist_track() {
        /* Arrange */
        val playlistManager = PlaylistManager(FakeSpotifyClient())

        /* Act */
        playlistManager.add("track1", "user1", VoteTypes.NONE)

        /* Assert */
        val playlist = playlistManager.getPlaylist("user1")
        Assert.assertEquals(1, playlist.tracks.size)
        Assert.assertEquals("track1", playlist.tracks.first().trackId)
    }

    @Test
    fun remove() {
        /* Arrange */
        val playlistManager = PlaylistManager(FakeSpotifyClient())

        /* Act */
        playlistManager.add("track1", "user1", VoteTypes.NONE)
        playlistManager.remove("track1", "user1")

        /* Assert */
        val playlist = playlistManager.getPlaylist("user1")
        Assert.assertEquals(1, playlist.tracks.size)
        Assert.assertEquals("track1", playlist.tracks.first().trackId)
        Assert.assertEquals(0, playlist.tracks.first().numVotes)
    }
}