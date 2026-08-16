package com.example.playlistmarket.di

import com.example.playlistmarket.utils.MediaPlayerWorker
import com.example.playlistmarket.utils.Timer
import org.koin.dsl.module

val utilsModule = module {
    factory { params ->
        MediaPlayerWorker(params.get(), params.get())
    }

    factory { params ->
        Timer(params.get(), params.get())
    }
}