package com.example.playlistmarket.SharedPreferencesPack

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmarket.TrackModel.Track
import com.google.gson.Gson


object SearchHistoryPreferencesWorker {

    private const val PREF_NAME = "search_history"
    const val KEY_HISTORY_LIST = "search_history_list"
    private const val MAX_COUNT_SAVED_TRACKS = 10
    private lateinit var preferences : SharedPreferences
    private val gson = Gson()
    val viewedTracks : List<Track>
        get() {
            val jsonViewedTracks = preferences.getString(KEY_HISTORY_LIST, "[]")
            val viewedTracksArray = gson.fromJson(jsonViewedTracks, Array<Track>::class.java)
            return viewedTracksArray?.toList() ?: emptyList()
        }


    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun addTrack(track: Track) {
        val modifiedViewedTracks = getListWithout(track)
        modifiedViewedTracks.add(track)
        if (modifiedViewedTracks.size > MAX_COUNT_SAVED_TRACKS) modifiedViewedTracks.removeAt(0)
        save(modifiedViewedTracks)
    }

    fun clearSearchTrackHistory() {
        save(mutableListOf<Track>())
    }

    private fun getListWithout(track: Track): MutableList<Track> {
        val modifiedViewedTracks = viewedTracks.toMutableList()
        val indexToRemove = modifiedViewedTracks.indexOfFirst {
            it.trackId == track.trackId
        }
        if (indexToRemove != -1) modifiedViewedTracks.removeAt(indexToRemove)
        return modifiedViewedTracks
    }


    private fun save(viewedTracks: MutableList<Track>) {
        val json = gson.toJson(viewedTracks)
        preferences.edit().putString(KEY_HISTORY_LIST, json).apply()
    }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}