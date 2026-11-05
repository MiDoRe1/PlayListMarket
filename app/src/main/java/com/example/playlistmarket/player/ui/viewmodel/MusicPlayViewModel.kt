package com.example.playlistmarket.player.ui.viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmarket.medialibrary.domain.api.FavoriteTracksInteractor
import com.example.playlistmarket.medialibrary.domain.api.PlaylistInteractor
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.utils.MediaPlayerWorker
import com.example.playlistmarket.utils.SingleLiveEvent
import com.example.playlistmarket.utils.Timer
import com.example.playlistmarket.utils.convertMillisecondsInNeededStringFormat
import com.example.playlistmarket.utils.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import org.koin.core.component.inject

class MusicPlayViewModel(
    private var track: Track,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
): ViewModel(), KoinComponent {

    private var onButtonsClickAllowed = true

    private val allowButtonClickFun = debounce<Boolean>(
        DEBOUNCE_TIME_ON_ALL_BUTTON_CLICK,
        viewModelScope,
        false
        ) {
        onButtonsClickAllowed = true
    }
    private var updateTimerJob: Job? = null

    private var playlists = mutableListOf<Playlist>()

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

    val playlistsLiveData = playlistInteractor.getAllPlaylists()
        .distinctUntilChanged()
        .asLiveData(viewModelScope.coroutineContext)



    init {
        mediaPlayerWorker.apply {
            setDataSource(track.previewUrl)
            prepareAsync()
        }

    }

    private val playerStatus = MutableLiveData<State>(State.Initialization)
    fun observePlayerStatus(): LiveData<State> = playerStatus

    private val singleEventVmRequest = SingleLiveEvent<VmRequest>()
    fun observeSingleEventVmRequest(): LiveData<VmRequest> = singleEventVmRequest

    private val addTrackInListStatus = MutableLiveData<AddTrackInPlaylistState>(
        AddTrackInPlaylistState.IdlingState)
    fun observeAddTrackInListStatus(): LiveData<AddTrackInPlaylistState> = addTrackInListStatus

    private val trackInformationStatus = MutableLiveData<Track>(track)

    fun getActualInformationAboutTrack() {
        val outdateTrack = track
        viewModelScope.launch {
            val actualTrack = favoriteTracksInteractor.getTrackById(track.trackId).firstOrNull()
            track =  actualTrack ?: outdateTrack
            trackInformationStatus.postValue(track)
        }
    }
    fun observeTrackInformationStatus(): LiveData<Track> = trackInformationStatus

    fun onPlayPauseButtonClick() {
        if (!allowedClick()) return
        if (playerStatus.value is State.ReadyToPlay) {
            playMusic()
        } else if (playerStatus.value is State.Playing) {
            pauseMusic()
        }
    }

    fun allowedClick(): Boolean {
        val currentState = onButtonsClickAllowed
        if (currentState) {
            onButtonsClickAllowed = false
            allowButtonClickFun(true)
        }
        return currentState
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

    fun changeFavoriteStatusOfTrack(){
        if (!allowedClick()) return
        val trackToUpdate = track.copy(isFavorites = !track.isFavorites)
        viewModelScope.launch {
            if (trackToUpdate.isFavorites) {
                favoriteTracksInteractor.insertTrack(trackToUpdate)
            } else {
                favoriteTracksInteractor.deleteTrack(trackToUpdate)
            }
            val updatedTrack = favoriteTracksInteractor.getTrackById(trackToUpdate.trackId).first()
            track = updatedTrack ?: trackToUpdate
            trackInformationStatus.postValue(track)
        }
    }



    fun addTrackInPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            if (playlistInteractor.isTrackInPlaylist(playlist, track)) {
                singleEventVmRequest.postValue(
                    VmRequest.ShowMessage("Трек уже добавлен в ${playlist.name}")
                )
            } else {
                playlistInteractor.insertTrackInPlaylist(playlist, track)
                singleEventVmRequest.postValue(
                    VmRequest.ShowMessage("Трек добавлен в ${playlist.name}")
                )
                addTrackInListStatus.postValue(AddTrackInPlaylistState.EndTransactionState)
            }
        }
    }

    fun cancelAddTrackInPlaylistProcess() {
        addTrackInListStatus.postValue(AddTrackInPlaylistState.IdlingState)
    }

        companion object {
            const val TIME_IN_MILLISECOND_TO_UPDATE_PLAY_TIMER = 300L
            const val INIT_TRACK_TIME = "00:00"

            const val DEBOUNCE_TIME_ON_ALL_BUTTON_CLICK = 300L

            const val MESSAGE_SUCCESS = ("Трек добавлен в")

        }
}