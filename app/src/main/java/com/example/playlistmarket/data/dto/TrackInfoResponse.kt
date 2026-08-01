package com.example.playlistmarket.data.dto

class TrackInfoResponse(
    val resultCount: Int?,
    val results: List<TrackInfoDto>?
) : Response()