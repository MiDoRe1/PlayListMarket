package com.example.playlistmarket.player.ui.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmarket.utils.MediaPlayerWorker
import com.example.playlistmarket.utils.Timer
import com.example.playlistmarket.utils.convertMillisecondsInNeededStringFormat

class MusicPlayViewModel(private val url: String): ViewModel() {
    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var  timer : Timer
    private lateinit var mediaPlayerWorker : MediaPlayerWorker

    private var currentTrackTime: String = INIT_TRACK_TIME

    private val onPreparedListener = MediaPlayer.OnPreparedListener {
        currentTrackTime = INIT_TRACK_TIME
        playerStatus.postValue(State.ReadyToPlay(currentTrackTime))
    }

    private val onCompletionListener = MediaPlayer.OnCompletionListener {
        currentTrackTime = INIT_TRACK_TIME
        playerStatus.postValue(State.ReadyToPlay(currentTrackTime))
    }

    private val callbackForTimer = Timer.OnTimeTickListener {
        if (playerStatus.value is State.Playing) {
            val currentTime = mediaPlayerWorker.currentPosition
            currentTrackTime = convertMillisecondsInNeededStringFormat(currentTime.toLong())
            playerStatus.postValue(State.Playing(currentTrackTime))
        }
    }

    init {
        mediaPlayerWorker = MediaPlayerWorker(
            onPreparedListener,
            onCompletionListener
        ).apply {
            setDataSource(url)
            prepareAsync()
        }

        timer = Timer(
            TIME_IN_MILLISECOND_TO_UPDATE_PLAY_TIMER,
            callbackForTimer
        )
    }

    private val playerStatus = MutableLiveData<State>(State.Initialization)
    fun observePlayerStatus(): LiveData<State> = playerStatus

    fun onPlayPauseButtonClick() {
        if (playerStatus.value is State.ReadyToPlay) {
            playMusic()
        } else if (playerStatus.value is State.Playing) {
            pauseMusic()
        }
    }

    private fun playMusic() {
        mediaPlayerWorker.play()
        if (mediaPlayerWorker.status == MediaPlayerWorker.CurrentStatusOfPlayer.PLAY) {
            playerStatus.postValue(State.Playing(currentTrackTime))
            timer.start()
        }
    }

    private fun pauseMusic() {
        mediaPlayerWorker.pause()
        if (mediaPlayerWorker.status == MediaPlayerWorker.CurrentStatusOfPlayer.PAUSE) {
            playerStatus.postValue(State.ReadyToPlay(currentTrackTime))
            timer.stop()
        }
    }

    fun forcedPause() {
        if (playerStatus.value is State.Playing) {
            pauseMusic()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerWorker.resume()
        timer.release()
    }

        companion object {
            val TIME_IN_MILLISECOND_TO_UPDATE_PLAY_TIMER = 1000L
            val INIT_TRACK_TIME = "00:00"

            fun getFactory(url: String) : ViewModelProvider.Factory = viewModelFactory {
                initializer {
                    MusicPlayViewModel(url)
                }
            }
        }
}