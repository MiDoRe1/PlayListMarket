package com.example.playlistmarket.data

import com.example.playlistmarket.data.dto.Response

interface TracksNetworkClient {

    fun doRequest(dto: Any) : Response
}