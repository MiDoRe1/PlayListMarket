package com.example.playlistmarket.search.data

import com.example.playlistmarket.search.data.dto.Response

interface NetworkClient {

    fun doRequest(dto: Any) : Response
}