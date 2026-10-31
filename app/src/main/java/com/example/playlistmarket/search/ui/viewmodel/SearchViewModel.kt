package com.example.playlistmarket.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarket.search.domain.api.interactors.SearchHistoryInteractor
import com.example.playlistmarket.search.domain.api.interactors.TracksInteractor
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.utils.SingleLiveEvent
import com.example.playlistmarket.utils.debounce
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val tracksInteractor: TracksInteractor
): ViewModel() {

    private var isMusicPlayerAllowed = true

    private var allowedMusicPlayerFun: (Boolean)->Unit = debounce<Boolean>(
            DEBOUNCE_MILLISECOND_TIME_TO_CLICK_ON_TRACK,
        viewModelScope ,
            false) { _ ->
            isMusicPlayerAllowed = true
    }

    private var specialScopeForDebouncedSearch = viewModelScope + CoroutineName("debounceScope")

    private var debouncedSearchTrackExecution: (expression: String)->Unit = debounce<String>(
        DEBOUNCE_MILLISECOND_TIME_TO_MAKE_REQUEST,
        specialScopeForDebouncedSearch,
        true) {
        expression ->
        executeSearchStateLogic(expression)
    }

    private var lastExpression: String? = null

    val stateViewModel = MutableLiveData<State>().apply {
        value = State.DefaultState
    }
    fun observeStateViewModel(): LiveData<State> = stateViewModel

    val singleEvent = SingleLiveEvent<Event>()

    fun observeSingleEventViewModel(): LiveData<Event> = singleEvent

    private fun getTracksFromHistory(): List<Track> {
        var historyTracks = listOf<Track>()
        searchHistoryInteractor.getHistory {
            tracks ->
            historyTracks = tracks
        }
        return historyTracks
    }

    fun findTracks(expression: String) {
        if (expression.isEmpty()) {
            specialScopeForDebouncedSearch.coroutineContext[Job]?.cancelChildren()
            executeHistoryStateLogic()
        } else if (lastExpression != expression || stateViewModel.value is State.ErrorState){
            lastExpression = expression
            stateViewModel.postValue(State.DefaultState)
            debouncedSearchTrackExecution(expression)
        }
    }

    private fun executeHistoryStateLogic() {
        val tracksFromHistory = getTracksFromHistory()
        if (tracksFromHistory.isEmpty()) {
            stateViewModel.postValue(
                State.DefaultState
            )
        } else {
            stateViewModel.postValue(
                State.HistoryState(tracksFromHistory)
            )
        }
    }

    private fun executeSearchStateLogic(expression: String) {
        stateViewModel.postValue(State.LoadingState)
        viewModelScope.launch {
            tracksInteractor
                .searchTracksWithFlow(expression)
                .collect {
                    (tracks, errorMessage) -> processResultRequest(tracks, errorMessage)
                }
        }
    }

    private fun processResultRequest(tracks: List<Track>?, errorMessage: String?) {
        if (errorMessage != null) {
            stateViewModel.postValue(State.ErrorState(errorMessage))
        } else if (tracks?.isNotEmpty() == true) {
            stateViewModel.postValue(State.FoundTracksState(tracks))
        } else {
            stateViewModel.postValue(State.EmptyResultState)
        }
    }

    private fun insertTrackInHistory(track: Track) {
        searchHistoryInteractor.saveToHistory(track)
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
        stateViewModel.postValue(State.DefaultState)
    }

    fun openMusicPlayer(track: Track) {
        if (isMusicPlayerAllowed) {
            isMusicPlayerAllowed = false
            insertTrackInHistory(track)
            if (stateViewModel.value is State.HistoryState) {
                executeHistoryStateLogic()
            }

            allowedMusicPlayerFun(true)
            initMusicPlayer(track)
        }
    }

    private fun initMusicPlayer(track: Track) {
        singleEvent.postValue(Event.MusicPlayerInvokeEvent(track))
    }

    companion object {

        const val DEBOUNCE_MILLISECOND_TIME_TO_MAKE_REQUEST: Long = 2000

        const val DEBOUNCE_MILLISECOND_TIME_TO_CLICK_ON_TRACK: Long = 1000

    }
}