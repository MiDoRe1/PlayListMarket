package com.example.playlistmarket.creator

import android.content.Context
import com.example.playlistmarket.search.data.SearchHistoryRepositoryImpl

import com.example.playlistmarket.search.data.TracksRepositoryImpl
import com.example.playlistmarket.utils.commonClients.impls.PrefsStorageClient
import com.example.playlistmarket.search.data.ITunesSearchAPI.ITunesSearchApiTracksNetworkClient
import com.example.playlistmarket.search.domain.api.interactors.SearchHistoryInteractor

import com.example.playlistmarket.search.domain.api.interactors.TracksInteractor
import com.example.playlistmarket.search.domain.api.repositories.SearchHistoryRepository
import com.example.playlistmarket.search.domain.api.repositories.TracksRepository
import com.example.playlistmarket.search.domain.impls.SearchHistoryInteractorImpl
import com.example.playlistmarket.search.domain.impls.TracksInteractorImpl
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.settings.data.SettingsRepositoryImpl
import com.example.playlistmarket.settings.domain.api.SettingsInteractor
import com.example.playlistmarket.settings.domain.api.SettingsRepository
import com.example.playlistmarket.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmarket.settings.domain.models.ThemeSettings
import com.example.playlistmarket.sharing.data.ExternalNavigatorImpl
import com.example.playlistmarket.sharing.domain.api.ExternalNavigator
import com.example.playlistmarket.sharing.domain.api.SharingInteractor
import com.example.playlistmarket.sharing.domain.impls.SharingInteractorImpl
import com.google.gson.reflect.TypeToken

object Creator {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context
    }
    private fun getTrackRepository(): TracksRepository {
        return TracksRepositoryImpl(
            ITunesSearchApiTracksNetworkClient()
        )
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTrackRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(
            PrefsStorageClient<ThemeSettings>(
                applicationContext,
                "app_settings",
                "current_theme",
                object : TypeToken<ThemeSettings>() {}.type
            ),
            applicationContext
        )
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(
            getSearchHistoryRepository(context)
        )
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<ArrayList<Track>>(
                context,
                PrefsStorageClient.Companion.SEARCH_HISTORY_KEY,
                PrefsStorageClient.Companion.SEARCH_HISTORY_KEY,
                object : TypeToken<ArrayList<Track>>() {}.type
            )
        )
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(
            getExternalNavigator(),
            getSettingsRepository()
        )
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(applicationContext)
    }
}