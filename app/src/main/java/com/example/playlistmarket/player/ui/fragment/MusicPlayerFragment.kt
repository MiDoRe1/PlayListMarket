package com.example.playlistmarket.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.R
import com.example.playlistmarket.core.ui.BindingFragment
import com.example.playlistmarket.databinding.FragmentMusicPlayerBinding
import com.example.playlistmarket.player.ui.viewmodel.MusicPlayViewModel
import com.example.playlistmarket.player.ui.viewmodel.State
import com.example.playlistmarket.search.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class MusicPlayerFragment : BindingFragment<FragmentMusicPlayerBinding>() {

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMusicPlayerBinding {
        return FragmentMusicPlayerBinding.inflate(inflater, container, false)
    }

    private lateinit var viewModel : MusicPlayViewModel

    private lateinit var currentTrack: Track

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTrack = Gson().fromJson(
            requireArguments().getString(JSON_FORMAT_TRACK_KEY),
            Track::class.java
        )

        viewModel = getViewModel { parametersOf(currentTrack.previewUrl) }

        viewModel.observePlayerStatus().observe(viewLifecycleOwner) {
            when (it) {
                is State.Initialization -> {

                }
                is State.Playing -> {
                    showPauseButton()
                    binding.textViewCurrentTimeOfTrack.text = it.timeOfTrack
                }
                is State.ReadyToPlay -> {
                    showPlayButton()
                    binding.textViewCurrentTimeOfTrack.text = it.timeOfTrack
                }
                is State.Pause -> {
                    showPlayButton()
                }

            }
        }

        initTrackIcon()
        initTrackInformation()
        initButtonReturn()
        initButtonPlay()

    }

    private fun initTrackIcon() {
        val roundedCornerValueInPx = resources.getDimension(R.dimen.default_rounded_corner_of_track_image_wide)
        Glide.with(binding.imgTrackIcon)
            .load(currentTrack.coverArtWorkUrl)
            .placeholder(R.drawable.img_track_placeholder_wide)
            .error(R.drawable.img_track_placeholder_wide)
            .transform(CenterCrop(), RoundedCorners(roundedCornerValueInPx.toInt()))
            .into(binding.imgTrackIcon)
    }

    private fun initTrackInformation() {
        binding.apply {
            textViewTrackName.text = currentTrack.trackName
            textViewGroupName.text = currentTrack.artistName
            textViewTrackDurationValue.text = currentTrack.trackTime
            textViewTrackAlbumValue.text = currentTrack.collectionName
            textViewTrackYearValue.text = currentTrack.year
            textViewTrackGenreValue.text = currentTrack.primaryGenreName
            textViewTrackCountryValue.text = currentTrack.country
        }
    }

    private fun initButtonReturn() {
        binding.buttonReturn.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun initButtonPlay() {
        binding.buttonPlay.setOnClickListener {
            viewModel.onPlayPauseButtonClick()
        }
    }

    private fun showPauseButton() {
        binding.buttonPlay.setImageResource(R.drawable.ic_pause)
    }

    private fun showPlayButton() {
        binding.buttonPlay.setImageResource(R.drawable.ic_play)
    }


    override fun onPause() {
        super.onPause()
        viewModel.forcedPause()
    }


    companion object{
        val JSON_FORMAT_TRACK_KEY : String = "trackInJsonFormat"

        fun getArgs(trackInJson: String): Bundle = Bundle().apply {
            putString(JSON_FORMAT_TRACK_KEY, trackInJson)
        }
    }

}