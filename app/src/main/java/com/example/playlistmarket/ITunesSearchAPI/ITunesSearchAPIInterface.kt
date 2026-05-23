package com.example.playlistmarket.ITunesSearchAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchAPIInterface {
    @GET("search")
    fun getTracks(@Query("term") text:String): Call<TrackInfoResponse>

}