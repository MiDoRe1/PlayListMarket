package com.example.playlistmarket.utils

import android.media.MediaPlayer

class MediaPlayerWorker(
    onPreparedListener: MediaPlayer.OnPreparedListener,
    onCompletionListener: MediaPlayer.OnCompletionListener) {

    private val mediaPlayer = MediaPlayer()
    var status = CurrentStatusOfPlayer.SET_DATA_SOURCE_NEEDED
    init {
        mediaPlayer.setOnPreparedListener{
            status = CurrentStatusOfPlayer.PREPARED
            onPreparedListener.onPrepared(mediaPlayer)
        }
        mediaPlayer.setOnCompletionListener{
            status = CurrentStatusOfPlayer.PREPARED
            onCompletionListener.onCompletion(mediaPlayer)
        }
    }


    val currentPosition : Int
        get() {
            var position = 0
            when (status) {
                CurrentStatusOfPlayer.PLAY, CurrentStatusOfPlayer.PAUSE -> {
                    position = mediaPlayer.currentPosition
                }
                else -> position = 0
            }
            return position
        }

    fun setDataSource(url: String) {
        mediaPlayer.setDataSource(url)
        status = CurrentStatusOfPlayer.DATA_SOURCE_SET
    }

    fun prepareAsync() {
        mediaPlayer.prepareAsync()
    }

    fun play() {
        mediaPlayer.start()
        status = CurrentStatusOfPlayer.PLAY
    }

    fun pause() {
        mediaPlayer.pause()
        status = CurrentStatusOfPlayer.PAUSE
    }

    fun resume() {
        mediaPlayer.release()
        status = CurrentStatusOfPlayer.SET_DATA_SOURCE_NEEDED
    }

    enum class CurrentStatusOfPlayer{
        SET_DATA_SOURCE_NEEDED,
        DATA_SOURCE_SET,
        PREPARED,
        READY_TO_PLAY,
        PLAY,
        PAUSE,
        STOPPED
    }
}
