package com.example.playlistmarket.data.network.ITunesSearchAPI



import com.example.playlistmarket.data.TracksNetworkClient
import com.example.playlistmarket.data.dto.TrackInfoRequest
import com.example.playlistmarket.data.dto.TrackInfoResponse
import com.example.playlistmarket.data.dto.Response as dtoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ITunesSearchApiTracksNetworkClient(
    val baseUrl: String = DEFAULT_URL_ITUNES
): TracksNetworkClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private  val iTunesService = retrofit.create<ITunesSearchAPIInterface>()

    fun asynGetData(
        request: TrackInfoRequest,
        outerOnResponse: OnResponseReactable,
        outerOnFailure: OnFailureReactable
    ){
        iTunesService.getTracks(request.expression).
        enqueue(object: Callback<TrackInfoResponse>{
            override fun onResponse(
                call: Call<TrackInfoResponse?>,
                response: Response<TrackInfoResponse?>
            ) {

                when (response.code()) {
                    200 -> outerOnResponse.doOnResponse(response.body())
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

    override fun doRequest(dto: Any): dtoResponse {
        if (dto is TrackInfoRequest) {
            val resp = iTunesService.getTracks(dto.expression).execute()
            val body = resp.body() ?: dtoResponse()
            return body.apply { resultCode = resp.code() }
        } else {
            return dtoResponse().apply { resultCode = 400 }
        }
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