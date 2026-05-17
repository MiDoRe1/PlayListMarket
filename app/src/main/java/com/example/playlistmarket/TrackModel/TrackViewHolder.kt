package com.example.playlistmarket.TrackModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.R

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView
    private val artistNameView: TextView // Имя исполнителя
    private val trackTimeView: TextView // Продолжительность трека
    private val artworkView: ImageView

    init {
        trackNameView = itemView.findViewById(R.id.track_name)
        artistNameView = itemView.findViewById(R.id.track_group)
        trackTimeView = itemView.findViewById(R.id.track_time)
        artworkView = itemView.findViewById(R.id.track_image)
    }

    constructor(parent: ViewGroup): this(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item, parent, false)
    )

    fun bind(model: Track) {
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = model.trackTime
        Glide.with(artworkView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.img_placeholder_track)
            .error(R.drawable.img_placeholder_track)
            .transform(RoundedCorners(2))
            .into(artworkView)
    }
}