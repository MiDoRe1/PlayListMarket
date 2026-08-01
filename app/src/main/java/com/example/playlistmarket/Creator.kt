package com.example.playlistmarket

import android.content.Context
import com.example.playlistmarket.data.SettingsRepositoryImpl
import com.example.playlistmarket.data.TracksRepositoryImpl
import com.example.playlistmarket.data.localStorage.SharedPreferencesPack.AppSettingsPreferencesWorker
import com.example.playlistmarket.data.localStorage.SharedPreferencesPack.TracksHistoryPreferencesWorker
import com.example.playlistmarket.data.network.ITunesSearchAPI.ITunesSearchApiTracksNetworkClient
import com.example.playlistmarket.domain.api.SettingsInteractor
import com.example.playlistmarket.domain.api.SettingsRepository
import com.example.playlistmarket.domain.api.TracksInteractor
import com.example.playlistmarket.domain.api.TracksRepository
import com.example.playlistmarket.domain.impls.SettingsInteractorImpl
import com.example.playlistmarket.domain.impls.TracksInteractorImpl

object Creator {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context
    }
    private fun getTrackRepository(): TracksRepository {
        return TracksRepositoryImpl(
            ITunesSearchApiTracksNetworkClient(),
            TracksHistoryPreferencesWorker.apply { this.init(applicationContext) })
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTrackRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(AppSettingsPreferencesWorker.apply {
            this.init(applicationContext)
        })
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }
}