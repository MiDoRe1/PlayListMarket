package com.example.playlistmarket.di

import com.example.playlistmarket.search.data.ITunesSearchAPI.ITunesSearchAPIInterface
import com.example.playlistmarket.search.data.ITunesSearchAPI.ITunesSearchApiTracksNetworkClient
import com.example.playlistmarket.search.data.NetworkClient
import com.example.playlistmarket.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmarket.search.data.TracksRepositoryImpl
import com.example.playlistmarket.search.domain.api.repositories.SearchHistoryRepository
import com.example.playlistmarket.search.domain.api.repositories.TracksRepository
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.settings.data.ResourceAndroidClient
import com.example.playlistmarket.settings.data.SettingsRepositoryImpl
import com.example.playlistmarket.settings.domain.api.SettingsRepository
import com.example.playlistmarket.settings.domain.models.ThemeSettings
import com.example.playlistmarket.sharing.data.ExternalNavigatorImpl
import com.example.playlistmarket.sharing.domain.api.ExternalNavigator
import com.example.playlistmarket.utils.commonClients.LocalStorageClient
import com.example.playlistmarket.utils.commonClients.impls.PrefsStorageClient
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchDataModule = module {

    single<ITunesSearchAPIInterface> { (baseUrl: String) ->
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
            .build().create(ITunesSearchAPIInterface::class.java)
    }

    single<NetworkClient> {
        ITunesSearchApiTracksNetworkClient(
            iTunesService = get {
                parametersOf(ITunesSearchApiTracksNetworkClient.DEFAULT_URL_ITUNES)
            }
        )
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<LocalStorageClient<ArrayList<Track>>>(named("localStoragePrefClientForSearchHistory")) {
        PrefsStorageClient<ArrayList<Track>>(
            androidContext(),
            PrefsStorageClient.SEARCH_HISTORY_PREF_NAME,
            PrefsStorageClient.SEARCH_HISTORY_KEY,
            object : TypeToken<ArrayList<Track>>() {}.type
        )
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            get(
                named("localStoragePrefClientForSearchHistory")
            )
        )
    }

}

val settingDataModule = module {

    single {
        ResourceAndroidClient(androidContext())
    }

    single<LocalStorageClient<ThemeSettings>>(named("LocalStorageForThemeSettings")) {
        PrefsStorageClient<ThemeSettings>(
            androidContext(),
            PrefsStorageClient.SETTINGS_PREF_NAME,
            PrefsStorageClient.SETTINGS_THEME_KEY,
            object : TypeToken<ThemeSettings>() {}.type
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            get(named("LocalStorageForThemeSettings")),
            androidContext(),
            get())
    }
}

val sharingDataModule = module {
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}