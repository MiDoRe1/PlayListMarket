package com.example.playlistmarket.utils.commonClients

interface LocalStorageClient<DTO> {
    fun getData(): DTO?
    fun saveData(dto: DTO)

}