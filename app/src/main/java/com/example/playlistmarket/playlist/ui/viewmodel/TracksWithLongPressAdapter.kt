package com.example.playlistmarket.playlist.ui.viewmodel

import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.search.ui.fragment.TrackAdapter
import com.example.playlistmarket.search.ui.fragment.TrackViewHolder

class TracksWithLongPressAdapter(
    tracks: List<Track>,
    onPress: (track: Track) -> Unit,
    private val onLongPress: (track: Track) -> Unit
): TrackAdapter(tracks, onPress){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackWithLongPressViewHolder = TrackWithLongPressViewHolder(parent, adapterOnTrackClickEnvelop) { position ->
        val track = tracks[position]
        onLongPress(track)
    }

}