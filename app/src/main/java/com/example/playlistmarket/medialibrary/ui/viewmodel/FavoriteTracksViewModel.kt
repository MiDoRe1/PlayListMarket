package com.example.playlistmarket.medialibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarket.medialibrary.domain.api.FavoriteTracksInteractor
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.search.ui.viewmodel.Event
import com.example.playlistmarket.search.ui.viewmodel.SearchViewModel.Companion.DEBOUNCE_MILLISECOND_TIME_TO_CLICK_ON_TRACK
import com.example.playlistmarket.search.ui.viewmodel.State
import com.example.playlistmarket.utils.SingleLiveEvent
import com.example.playlistmarket.utils.debounce
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext

class FavoriteTracksViewModel(
    val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private var isMusicPlayerAllowed = true
    private var allowedMusicPlayerFun: (Boolean)->Unit = debounce<Boolean>(
        DEBOUNCE_MILLISECOND_TIME_TO_CLICK_ON_TRACK,
        viewModelScope ,
        false) { _ ->
        isMusicPlayerAllowed = true
    }

    private val stateViewModel = MutableLiveData<FavoriteTracksStates>().apply {
        value = FavoriteTracksStates.DefaultState
    }

    fun observeStateViewModel(): LiveData<FavoriteTracksStates> = stateViewModel

    private val singleEventRequestToUIState = SingleLiveEvent<RequestToUIState>()

    fun observeSingleEventRequestToUIState(): LiveData<RequestToUIState> = singleEventRequestToUIState


    fun getFavoritesTracks() {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect {
                tracks -> processResult(tracks)
            }
        }
    }

    fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            stateViewModel.postValue(FavoriteTracksStates.EmptyResultState)
        } else {
            stateViewModel.postValue(FavoriteTracksStates.ShowFavoriteTracks(tracks))
        }
    }

    fun openMusicPlayer(track: Track) {
        if (isMusicPlayerAllowed) {
            isMusicPlayerAllowed = false
            allowedMusicPlayerFun(true)
            initMusicPlayer(track)
        }
    }

    private fun initMusicPlayer(track: Track) {
        singleEventRequestToUIState.postValue(RequestToUIState.OpenMusicPlayerRequestState(track))
    }

}