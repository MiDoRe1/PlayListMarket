package com.example.playlistmarket.search.data.ITunesSearchAPI



import com.example.playlistmarket.search.data.NetworkClient
import com.example.playlistmarket.search.data.dto.TrackInfoRequest
import com.example.playlistmarket.search.data.dto.TrackInfoResponse
import com.example.playlistmarket.search.data.dto.Response as dtoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ITunesSearchApiTracksNetworkClient(
    private val iTunesService: ITunesSearchAPIInterface
): NetworkClient {

    fun asyncGetData(
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
        try {
            if (dto is TrackInfoRequest) {
                val resp = iTunesService.getTracks(dto.expression).execute()
                val body = resp.body() ?: dtoResponse()
                return body.apply { resultCode = resp.code() }
            } else {
                return dtoResponse().apply { resultCode = 400 }
            }
        } catch (e: UnknownHostException) {
            return dtoResponse().apply { resultCode = -1 }
        } catch (e: SocketTimeoutException) {
            return dtoResponse().apply { resultCode = -1 }
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