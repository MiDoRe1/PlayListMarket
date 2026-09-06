package com.example.playlistmarket.search.ui.fragment

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.R
import com.example.playlistmarket.databinding.TrackItemBinding
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.utils.dpToPx

class TrackViewHolder(
    private val binding: TrackItemBinding,
    private val onItemClick: (position: Int)-> Unit
) : RecyclerView.ViewHolder(binding.root) {


    init {
        binding.root.setOnClickListener {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClick(position)
            }
        }
    }

    constructor(parent: ViewGroup, onItemClick: (position: Int) -> Unit): this(
        TrackItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onItemClick
    )


    fun bind(model: Track) {
        binding.trackName.text = model.trackName
        binding.trackGroup.text = model.artistName
        binding.trackTime.text = model.trackTime
        loadArtworkView(model.artworkUrl100)
    }

    private fun loadArtworkView(artworkUrl100: String) {
        val pxValueOfRoundedCorner = dpToPx(
            getDpSizeForRoundedCornerOfTrackImage(),
            binding.trackImage.context
        )
        Glide.with(binding.trackImage)
            .load(artworkUrl100)
            .placeholder(R.drawable.img_track_placeholder)
            .error(R.drawable.img_track_placeholder)
            .transform(CenterCrop(), RoundedCorners(pxValueOfRoundedCorner))
            .into(binding.trackImage)
    }
    private fun getDpSizeForRoundedCornerOfTrackImage(): Float {
        val outValue = TypedValue()
        binding.trackImage.context.resources.getValue(
            R.dimen.default_rounded_corner_of_track_image,
            outValue,
            true
        )
        val dpValue = TypedValue.complexToFloat(outValue.data)
        return dpValue
    }
}