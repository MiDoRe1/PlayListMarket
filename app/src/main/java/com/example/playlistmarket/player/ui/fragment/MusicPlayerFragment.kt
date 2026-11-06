package com.example.playlistmarket.player.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.R
import com.example.playlistmarket.core.ui.BindingFragment
import com.example.playlistmarket.databinding.FragmentMusicPlayerBinding
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.player.ui.viewmodel.AddTrackInPlaylistState
import com.example.playlistmarket.player.ui.viewmodel.MusicPlayViewModel
import com.example.playlistmarket.player.ui.viewmodel.State
import com.example.playlistmarket.player.ui.viewmodel.VmRequest
import com.example.playlistmarket.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue
import kotlin.math.roundToInt

class MusicPlayerFragment : BindingFragment<FragmentMusicPlayerBinding>() {

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMusicPlayerBinding {
        return FragmentMusicPlayerBinding.inflate(inflater, container, false)
    }

    private lateinit var viewModel : MusicPlayViewModel

    private lateinit var currentTrack: Track

    private  var playlists = mutableListOf<Playlist>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTrack = Gson().fromJson(
            requireArguments().getString(JSON_FORMAT_TRACK_KEY),
            Track::class.java
        )

        initViewModels()
        initTrackIcon()
        initTrackInformation()
        initButtonReturn()
        initButtonPlay()
        initButtonFavoriteTrack()
        initButtonAddTrackInPlaylist()
        initBottomSheetUI()

        viewModel.getActualInformationAboutTrack()
    }

    private fun initViewModels() {
        viewModel = getViewModel { parametersOf(currentTrack) }

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

        viewModel.observeTrackInformationStatus().observe(viewLifecycleOwner) { track ->
            updateTrackInformationUI(track)
        }

        viewModel.observeSingleEventVmRequest().observe(viewLifecycleOwner) {request ->
            when(request) {
                is VmRequest.ShowMessage -> {
                    Toast
                        .makeText(requireContext(), request.message, Toast.LENGTH_SHORT)
                        .show()
                    if (request.message.contains(MusicPlayViewModel.MESSAGE_SUCCESS)) {
                        BottomSheetBehavior.from(binding.bottomSheetAddTrackInPlaylist).state =
                            BottomSheetBehavior.STATE_HIDDEN
                    }
                }

            }
        }




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
        initFavoriteIcon()
    }
    private fun initFavoriteIcon() {
        if (currentTrack.isFavorites) {
            binding.buttonLike.setImageResource(R.drawable.ic_like_active)
        } else {
            binding.buttonLike.setImageResource(R.drawable.ic_like_unactive)
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

    private fun initButtonFavoriteTrack() {
        binding.buttonLike.setOnClickListener {
            viewModel.changeFavoriteStatusOfTrack()
        }
    }
    private fun initButtonAddTrackInPlaylist() {
        binding.buttonAddInAlbum.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheetAddTrackInPlaylist).state =
                BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    private fun initBottomSheetUI() {

        initPlaylistsRv()
        viewModel.playlistsLiveData.observe(viewLifecycleOwner){ newPlaylists ->
            updatePlaylistsRV(newPlaylists)
        }
        initButtonCratePlaylist()
        binding.overlay.visibility = View.GONE
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetAddTrackInPlaylist)
        bottomSheetBehavior.apply {
            isFitToContents=true
            skipCollapsed=true
            state=BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // newState — новое состояние BottomSheet
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.visibility = View.VISIBLE

                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.visibility = View.VISIBLE

                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // возобновляем трейлер
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        // Остальные состояния не обрабатываем
                    }
                }
            }

            override fun onSlide(p0: View, p1: Float) {
                binding.overlay.alpha = if (p1 > 0.7f) p1 else 0.7f

            }


        })
    }

    private fun initPlaylistsRv() {
        binding.recyclerviewPlaylists.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerviewPlaylists.adapter = PlaylistHorizontalAdapter(
            playlists,
            {playlist ->
                viewModel.addTrackInPlaylist(playlist)
            }
        )
    }
    private fun initButtonCratePlaylist() {
        binding.buttonCreateNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_musicPlayerFragment_to_addPlaylistFragment)
        }
    }

    private fun showPauseButton() {
        binding.buttonPlay.setImageResource(R.drawable.ic_pause)
    }

    private fun showPlayButton() {
        binding.buttonPlay.setImageResource(R.drawable.ic_play)
    }

    private fun updateTrackInformationUI(track: Track) {
        currentTrack = track
        initTrackInformation()
    }
    private fun updatePlaylistsRV(newPlaylists: List<Playlist>) {
        Log.d("sync", "${newPlaylists.toString()}")
        playlists.clear()
        playlists.addAll(newPlaylists)
        binding.recyclerviewPlaylists.adapter?.notifyDataSetChanged()

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