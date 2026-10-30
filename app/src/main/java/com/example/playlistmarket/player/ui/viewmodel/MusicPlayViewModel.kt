package com.example.playlistmarket.player.ui.viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarket.utils.MediaPlayerWorker
import com.example.playlistmarket.utils.Timer
import com.example.playlistmarket.utils.convertMillisecondsInNeededStringFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import org.koin.core.component.inject

class MusicPlayViewModel(
    private val url: String
): ViewModel(), KoinComponent {

    private var updateTimerJob: Job? = null

    private suspend fun updateTimer() {
        if (playerStatus.value is State.Playing) {
            delay(TIME_IN_MILLISECOND_TO_UPDATE_PLAY_TIMER)
            val currentTime = mediaPlayerWorker.currentPosition
            currentTrackTime = convertMillisecondsInNeededStringFormat(currentTime.toLong())
            playerStatus.postValue(State.Playing(currentTrackTime))
        }
    }

    private val mediaPlayerWorker : MediaPlayerWorker by inject {
        parametersOf(
            onPreparedListener,
            onCompletionListener
        )
    }

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
        mediaPlayerWorker.apply {
            setDataSource(url)
            prepareAsync()
        }
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
            updateTimerJob = viewModelScope.launch {
                while(mediaPlayerWorker.status == MediaPlayerWorker.CurrentStatusOfPlayer.PLAY){
                    Log.d("Music", mediaPlayerWorker.status.toString())
                    delay(TIME_IN_MILLISECOND_TO_UPDATE_PLAY_TIMER)
                    updateTimer()
                }
            }
        }
    }

    private fun pauseMusic() {
        mediaPlayerWorker.pause()
        if (mediaPlayerWorker.status == MediaPlayerWorker.CurrentStatusOfPlayer.PAUSE) {
            playerStatus.postValue(State.ReadyToPlay(currentTrackTime))
            updateTimerJob?.cancel()
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
        updateTimerJob?.cancel()
    }

        companion object {
            const val TIME_IN_MILLISECOND_TO_UPDATE_PLAY_TIMER = 300L
            const val INIT_TRACK_TIME = "00:00"

        }
}