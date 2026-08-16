package com.example.playlistmarket.search.data.dto

class TrackInfoResponse(
    val resultCount: Int?,
    val results: List<TrackInfoDto>?
) : Response()