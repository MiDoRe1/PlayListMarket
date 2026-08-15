package com.example.playlistmarket.search.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmarket.creator.Creator
import com.example.playlistmarket.player.ui.activity.MusicPlayActivity
import com.example.playlistmarket.search.domain.api.interactors.SearchHistoryInteractor
import com.example.playlistmarket.search.domain.api.interactors.TracksInteractor
import com.example.playlistmarket.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Runnable

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val tracksInteractor: TracksInteractor
): ViewModel() {

    private var isMusicPlayerAllowed = true

    private var lastExpression: String? = null
    private var mainHandler = Handler(Looper.getMainLooper())

    val stateViewModel = MutableLiveData<State>().apply {
        value = State.DefaultState
    }
    fun observeStateViewModel(): LiveData<State> = stateViewModel

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
            mainHandler.removeCallbacksAndMessages(this)
            executeHistoryStateLogic()
        } else if (lastExpression != expression || stateViewModel.value is State.ErrorState){
            lastExpression = expression
            stateViewModel.postValue(State.DefaultState)
            mainHandler.removeCallbacksAndMessages(this)
            mainHandler.postDelayed(
                Runnable {executeSearchStateLogic(expression)},
                this,
                DEBOUNCE_MILLISECOND_TIME_TO_MAKE_REQUEST
            )
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
        tracksInteractor.searchTracks(expression) {
                tracks, errorMessage ->
            if (errorMessage != null) {
                stateViewModel.postValue(State.ErrorState(errorMessage))
            } else if (tracks?.isNotEmpty() == true) {
                stateViewModel.postValue(State.FoundTracksState(tracks))
            } else {
                stateViewModel.postValue(State.EmptyResultState)
            }
        }
    }

    fun insertTrackInHistory(track: Track) {
        searchHistoryInteractor.saveToHistory(track)
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
        stateViewModel.postValue(State.DefaultState)
    }

    fun openMusicPlayer(track: Track, context: Context) {
        if (isMusicPlayerAllowed) {
            isMusicPlayerAllowed = false
            insertTrackInHistory(track)
            if (stateViewModel.value is State.HistoryState) {
                executeHistoryStateLogic()
            }
            mainHandler.postDelayed(
                Runnable{ isMusicPlayerAllowed = true},
                DEBOUNCE_MILLISECOND_TIME_TO_CLICK_ON_TRACK
            )
            initMusicPlayer(track, context)
        }
    }

    private fun initMusicPlayer(track: Track, context: Context) {
        val musicPlayerIntent = Intent(context, MusicPlayActivity::class.java)
        musicPlayerIntent.putExtra(
            MusicPlayActivity.Companion.JSON_FORMAT_TRACK_KEY,
            Gson().toJson(track)
        )
        musicPlayerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(musicPlayerIntent)
    }

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                SearchViewModel(
                    Creator.provideSearchHistoryInteractor(app),
                    Creator.provideTracksInteractor()
                )
            }
        }

        const val DEBOUNCE_MILLISECOND_TIME_TO_MAKE_REQUEST: Long = 2000

        const val DEBOUNCE_MILLISECOND_TIME_TO_CLICK_ON_TRACK: Long = 1000

    }
}