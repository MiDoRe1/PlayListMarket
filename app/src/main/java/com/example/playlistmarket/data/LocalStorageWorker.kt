package com.example.playlistmarket.data

import com.example.playlistmarket.domain.api.OnChangesRegisterable


interface LocalStorageWorker<DTO> {
    fun getData(): DTO
    fun saveData(dto: DTO)

   fun registerOnChanges(listener: OnChangesRegisterable.Listener)

   fun unregisterOnChanges(listener: OnChangesRegisterable.Listener)
}