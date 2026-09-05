package com.example.playlistmarket.di

import com.example.playlistmarket.medialibrary.ui.viewmodel.FavoriteTracksViewModel
import com.example.playlistmarket.medialibrary.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmarket.player.ui.viewmodel.MusicPlayViewModel
import com.example.playlistmarket.search.ui.viewmodel.SearchViewModel
import com.example.playlistmarket.settings.ui.viewmodel.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel<SearchViewModel> {
        SearchViewModel(get(), get())
    }

    viewModel<MusicPlayViewModel> { (url: String) ->
        MusicPlayViewModel(url)
    }

    viewModel<SettingViewModel> {
        SettingViewModel(get(), get())
    }

    viewModel<FavoriteTracksViewModel> {
        FavoriteTracksViewModel()
    }

    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel()
    }

}

