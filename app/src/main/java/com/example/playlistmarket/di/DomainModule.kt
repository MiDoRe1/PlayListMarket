package com.example.playlistmarket.di

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