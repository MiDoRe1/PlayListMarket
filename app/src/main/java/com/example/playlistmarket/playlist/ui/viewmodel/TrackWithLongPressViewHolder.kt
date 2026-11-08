package com.example.playlistmarket.playlist.ui.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playlistmarket.databinding.TrackItemBinding
import com.example.playlistmarket.player.ui.fragment.PlaylistItemHorizontalViewHolder
import com.example.playlistmarket.search.ui.fragment.TrackViewHolder

class TrackWithLongPressViewHolder(
    binding: TrackItemBinding,
    onPress: (position: Int) -> Unit,
    private val onLongPress: (position: Int) -> Unit

): TrackViewHolder(binding, onPress) {

    init {
        binding.root.setOnLongClickListener {
            val position = bindingAdapterPosition
            return@setOnLongClickListener if (position != RecyclerView.NO_POSITION) {
                onLongPress(position)
                true
            } else false
        }
    }

    constructor(
        parent: ViewGroup,
        onPress: (position: Int) -> Unit,
        onLongPress: (position: Int) -> Unit
    ): this (
        TrackItemBinding.inflate(LayoutInflater.from(parent.context),parent, false),
        onPress,
        onLongPress
    )
}