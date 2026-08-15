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

    private val onPreparedListener = MediaPlayer.OnPreparedListener {
        playerStatus.postValue(State.ReadyToPlay)
    }

    private val onCompletionListener = MediaPlayer.OnCompletionListener {
        playerStatus.postValue(State.ReadyToPlay)
    }

    private val callbackForTimer = Timer.OnTimeTickListener {
        if (playerStatus.value is State.Playing) {
            val currentTime = mediaPlayerWorker.currentPosition
            val currentTimeToString = convertMillisecondsInNeededStringFormat(currentTime.toLong())
            timeLiveData.postValue(currentTimeToString)
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


    private val timeLiveData = MutableLiveData<String>("00:00")
    fun observeTimeLiveData(): LiveData<String> = timeLiveData

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
            playerStatus.postValue(State.Playing)
            timer.start()
        }
    }

    private fun pauseMusic() {
        mediaPlayerWorker.pause()
        if (mediaPlayerWorker.status == MediaPlayerWorker.CurrentStatusOfPlayer.PAUSE) {
            playerStatus.postValue(State.ReadyToPlay)
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

            fun getFactory(url: String) : ViewModelProvider.Factory = viewModelFactory {
                initializer {
                    MusicPlayViewModel(url)
                }
            }
        }
}