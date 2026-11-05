package com.example.playlistmarket.player.ui.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarket.medialibrary.domain.models.Playlist

class PlaylistHorizontalAdapter(
    private val playlists: List<Playlist>,
    private val onItemClick: (playlist: Playlist) -> Unit
): RecyclerView.Adapter<PlaylistItemHorizontalViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistItemHorizontalViewHolder =
        PlaylistItemHorizontalViewHolder(
            parent,
            {position ->
                onItemClick(playlists[position])
            }
        )


    override fun onBindViewHolder(
        holder: PlaylistItemHorizontalViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}