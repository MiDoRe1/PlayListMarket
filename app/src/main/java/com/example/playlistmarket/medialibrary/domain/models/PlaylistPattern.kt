package com.example.playlistmarket.medialibrary.domain.models

import kotlinx.serialization.descriptors.SerialDescriptor

class PlaylistPattern(
    var name: String = "",
    var description: String = "",
    var countOfTracksIn: Long = 0,
    var coverUrl: String = ""
) {
    constructor(playlist: Playlist): this(playlist.name,playlist.description, playlist.countOfTracksIn, playlist.coverUrl)
}