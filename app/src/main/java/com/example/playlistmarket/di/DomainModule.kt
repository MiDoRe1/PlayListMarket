package com.example.playlistmarket.di

import com.example.playlistmarket.medialibrary.data.FavoriteTracksRepositoryImpl
import com.example.playlistmarket.medialibrary.data.PlaylistRepositoryImpl
import com.example.playlistmarket.medialibrary.domain.api.FavoriteTracksInteractor
import com.example.playlistmarket.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmarket.medialibrary.domain.api.PlaylistInteractor
import com.example.playlistmarket.medialibrary.domain.api.PlaylistRepository
import com.example.playlistmarket.medialibrary.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlistmarket.medialibrary.domain.impl.PlaylistInteractorImpl
import com.example.playlistmarket.search.domain.api.interactors.SearchHistoryInteractor
import com.example.playlistmarket.search.domain.api.interactors.TracksInteractor
import com.example.playlistmarket.search.domain.impls.SearchHistoryInteractorImpl
import com.example.playlistmarket.search.domain.impls.TracksInteractorImpl
import com.example.playlistmarket.settings.domain.api.SettingsInteractor
import com.example.playlistmarket.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmarket.sharing.domain.api.SharingInteractor
import com.example.playlistmarket.sharing.domain.impls.SharingInteractorImpl
import org.koin.dsl.module

val searchDomainModule = module {
    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }
}

val settingDomainModule = module {
    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}

val sharingDomainModule = module {
    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }
}

val mediaLibraryDomainModule = module {
    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }
}