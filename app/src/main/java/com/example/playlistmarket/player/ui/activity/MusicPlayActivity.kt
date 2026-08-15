package com.example.playlistmarket.player.ui.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.R
import com.example.playlistmarket.databinding.ActivityMusicPlayBinding
import com.example.playlistmarket.player.ui.viewmodel.MusicPlayViewModel
import com.example.playlistmarket.player.ui.viewmodel.State
import com.example.playlistmarket.search.domain.models.Track
import com.google.gson.Gson

class MusicPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlayBinding

    private lateinit var viewModel : MusicPlayViewModel

    private lateinit var currentTrack: Track


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMusicPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentTrack = Gson().fromJson<Track>(
            intent.getStringExtra(JSON_FORMAT_TRACK_KEY),
            Track::class.java)

        viewModel = ViewModelProvider.create(
            this,
            MusicPlayViewModel.getFactory(currentTrack.previewUrl)
        ).get(MusicPlayViewModel::class.java)

        viewModel.observeTimeLiveData().observe(this) {
            binding.textViewCurrentTimeOfTrack.text = it
        }

        viewModel.observePlayerStatus().observe(this) {
            when (it) {
                is State.Initialization -> {

                }
                is State.Playing -> {
                    showPauseButton()
                }
                is State.ReadyToPlay -> {
                    showPlayButton()
                }
                is State.Pause -> {
                    showPlayButton()
                }

            }
        }
        initTrackIcon()
        initTrackInformation()
        initButtonReturn()
        initTextViewCurrentTimeOfTrack()
        initButtonPlay()

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
    private fun initTextViewCurrentTimeOfTrack() {

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
            this@MusicPlayActivity.finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.forcedPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object{
        val JSON_FORMAT_TRACK_KEY : String = "trackInJsonFormat"
    }
}