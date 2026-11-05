package com.example.playlistmarket.player.ui.viewmodel

sealed class VmRequest {
    data class ShowMessage(var message: String): VmRequest()
}