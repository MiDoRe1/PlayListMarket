package com.example.playlistmarket.di

import com.example.playlistmarket.medialibrary.ui.viewmodel.AddPlaylistViewModel
import com.example.playlistmarket.medialibrary.ui.viewmodel.FavoriteTracksViewModel
import com.example.playlistmarket.medialibrary.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmarket.player.ui.viewmodel.MusicPlayViewModel
import com.example.playlistmarket.playlist.ui.viewmodel.PlaylistViewModel
import com.example.playlistmarket.search.ui.viewmodel.SearchViewModel
import com.example.playlistmarket.settings.ui.viewmodel.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.playlistmarket.search.domain.models.Track
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext

val viewModelModule = module {
    viewModel<SearchViewModel> {
        SearchViewModel(get(), get())
    }

    viewModel<MusicPlayViewModel> { (track: Track) ->
        MusicPlayViewModel(track, get(), get())
    }

    viewModel<SettingViewModel> {
        SettingViewModel(get(), get())
    }

    viewModel<FavoriteTracksViewModel> {
        FavoriteTracksViewModel(get())
    }

    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel(get())
    }

    viewModel<AddPlaylistViewModel> {
        AddPlaylistViewModel(get(), get())
    }

    viewModel<PlaylistViewModel> { (playlist_id: Long) ->
        PlaylistViewModel(get(), playlist_id)
    }

}

