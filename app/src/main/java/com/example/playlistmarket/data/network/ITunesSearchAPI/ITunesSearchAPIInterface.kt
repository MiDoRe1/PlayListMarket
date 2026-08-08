package com.example.playlistmarket.data.network.ITunesSearchAPI

import com.example.playlistmarket.data.dto.TrackInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchAPIInterface {
    @GET("search?entity=song")
    fun getTracks(@Query("term") text:String): Call<TrackInfoResponse>

}