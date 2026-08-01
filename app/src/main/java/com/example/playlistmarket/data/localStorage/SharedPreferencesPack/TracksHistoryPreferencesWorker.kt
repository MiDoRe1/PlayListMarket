package com.example.playlistmarket.data.localStorage.SharedPreferencesPack


import com.example.playlistmarket.data.dto.TracksHistoryDto
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmarket.data.LocalStorageWorker
import com.example.playlistmarket.domain.api.OnChangesRegisterable
import com.example.playlistmarket.domain.models.Track
import com.google.gson.Gson


object TracksHistoryPreferencesWorker: LocalStorageWorker<TracksHistoryDto> {

    private val listenersMap = mutableMapOf<OnChangesRegisterable.Listener, SharedPreferences.OnSharedPreferenceChangeListener>()

    private const val PREF_NAME = "search_history"
    const val KEY_HISTORY_LIST = "search_history_list"
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


    fun clearSearchTrackHistory() {
        save(mutableListOf())
    }



    private fun save(viewedTracks: List<Track>) {
        val json = gson.toJson(viewedTracks)
        preferences.edit().putString(KEY_HISTORY_LIST, json).apply()
    }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }


    override fun getData(): TracksHistoryDto {
        return TracksHistoryDto(viewedTracks)
    }

    override fun saveData(dto: TracksHistoryDto) {
        save(dto.tracks)
    }

    override fun registerOnChanges(listener: OnChangesRegisterable.Listener) {
        val androidListener = SharedPreferences.OnSharedPreferenceChangeListener {
            _, key ->
            if (key == KEY_HISTORY_LIST) listener.callback()
        }

        listenersMap[listener] = androidListener
        preferences.registerOnSharedPreferenceChangeListener(androidListener)
    }

    override fun unregisterOnChanges(listener: OnChangesRegisterable.Listener) {
        val androidListener = listenersMap.remove(listener)
        if (androidListener != null) {
            preferences.unregisterOnSharedPreferenceChangeListener(androidListener)
        }
    }



}