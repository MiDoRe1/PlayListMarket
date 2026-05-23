package com.example.playlistmarket.ITunesSearchAPI



import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

class ITunesSearchAPIWorker(baseUrl: String = DEFAULT_URL_ITUNES) {
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private  val iTunesService = retrofit.create<ITunesSearchAPIInterface>()

    fun getData(
        name: String,
        outerOnRespone: OnResponseReactable,
        outerOnFailure: OnFailureReactable
    ){
        iTunesService.getTracks(name).
        enqueue(object: Callback<TrackInfoResponse>{
            override fun onResponse(
                call: Call<TrackInfoResponse?>,
                response: Response<TrackInfoResponse?>
            ) {

                when (response.code()) {
                    200 -> outerOnRespone.doOnResponse(response.body())
                    else -> outerOnFailure.doOnFailure()
                }

            }

            override fun onFailure(
                call: Call<TrackInfoResponse?>,
                t: Throwable
            ) {
                outerOnFailure.doOnFailure()
            }

        })
    }

    fun interface OnResponseReactable {
        fun doOnResponse(tracksData: TrackInfoResponse?)
    }

    fun interface OnFailureReactable {
        fun doOnFailure()
    }

    companion object {
        const val DEFAULT_URL_ITUNES : String =  "https://itunes.apple.com/"
    }

}