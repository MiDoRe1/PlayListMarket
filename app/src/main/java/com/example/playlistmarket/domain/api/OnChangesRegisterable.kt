package com.example.playlistmarket.domain.api

interface OnChangesRegisterable {
    fun registerOnChanges(listener: Listener)

    fun unregisterOnChanges(listener: Listener)

    fun interface Listener {
        fun callback()
    }
}