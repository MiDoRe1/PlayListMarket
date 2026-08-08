package com.example.playlistmarket.ui.searchScreen

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.R
import com.example.playlistmarket.domain.models.Track
import com.example.playlistmarket.utils.dpToPx

class TrackViewHolder(
    itemView: View,
    private val onItemClick: (position: Int)-> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val trackNameView: TextView
    private val artistNameView: TextView // Имя исполнителя
    private val trackTimeView: TextView // Продолжительность трека
    private val artworkView: ImageView

    init {
        trackNameView = itemView.findViewById(R.id.track_name)
        artistNameView = itemView.findViewById(R.id.track_group)
        trackTimeView = itemView.findViewById(R.id.track_time)
        artworkView = itemView.findViewById(R.id.track_image)
        itemView.setOnClickListener {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClick(position)
            }
        }
    }

    constructor(parent: ViewGroup, onItemClick: (position: Int) -> Unit): this(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item, parent, false),
        onItemClick
    )

    fun bind(model: Track) {
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = model.trackTime
        loadArtworkView(model.artworkUrl100)
    }

    private fun loadArtworkView(artworkUrl100: String) {
        val pxValueOfRoundedCorner = dpToPx(
            getDpSizeForRoundedCornerOfTrackImage(),
            artworkView.context
        )
        Glide.with(artworkView)
            .load(artworkUrl100)
            .placeholder(R.drawable.img_track_placeholder)
            .error(R.drawable.img_track_placeholder)
            .transform(CenterCrop(), RoundedCorners(pxValueOfRoundedCorner))
            .into(artworkView)
    }
    private fun getDpSizeForRoundedCornerOfTrackImage(): Float {
        val outValue = TypedValue()
        artworkView.context.resources.getValue(
            R.dimen.default_rounded_corner_of_track_image,
            outValue,
            true
        )
        val dpValue = TypedValue.complexToFloat(outValue.data)
        return dpValue
    }
}