package com.example.playlistmarket.medialibrary.domain.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val countOfTracksIn: Long = 0,
    val coverUrl: String = ""
)
