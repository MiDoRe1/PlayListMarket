package com.example.playlistmarket.playlist.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.R
import com.example.playlistmarket.core.ui.BindingFragment
import com.example.playlistmarket.databinding.FragmentPlaylistBinding
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.medialibrary.ui.fragment.AddPlaylistFragment
import com.example.playlistmarket.player.ui.fragment.MusicPlayerFragment
import com.example.playlistmarket.player.ui.fragment.PlaylistHorizontalAdapter
import com.example.playlistmarket.playlist.ui.viewmodel.PlaylistDataViewModelState
import com.example.playlistmarket.playlist.ui.viewmodel.PlaylistViewModel
import com.example.playlistmarket.playlist.ui.viewmodel.TracksWithLongPressAdapter
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.search.ui.fragment.TrackAdapter
import com.example.playlistmarket.utils.convertMillisecondsInNeededStringFormat
import com.example.playlistmarket.utils.getMinutePlural
import com.example.playlistmarket.utils.getTracksPlural
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchView
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment: BindingFragment<FragmentPlaylistBinding>() {

    private lateinit var viewModel: PlaylistViewModel
    private lateinit var playlist: Playlist
    private val tracks = mutableListOf<Track>()
    private val playlistRv = mutableListOf<Playlist>()
    private var totalTime = ""

    private val onTrackPress = {track: Track ->
    findNavController().navigate(R.id.action_playlistFragment_to_musicPlayerFragment,
    MusicPlayerFragment.getArgs(Gson().toJson(track)))
    }

    private val onTrackLongPress = {track: Track ->
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_track_question)
            .setPositiveButton(R.string.yes) { dialog, which ->
                viewModel.deleteTrackFromPlaylist(track)
            }
            .setNegativeButton(R.string.no) { dialog, which ->
                dialog.dismiss()
            }.show()
    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val track_id = requireArguments().getLong(PLAYLIST_ID_BUNDLE_KEY)
        viewModel = getViewModel { parametersOf(track_id) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarHeader()
        initRvTracks()
        initShareButton()
        initButtonSettings()
        initSettingBottomSheet()
        observeDataFromViewModel()
        lifecycleScope.launch {
            viewModel.updateData()
        }
    }

    private fun initToolbarHeader(){
        binding.toolbarHeader.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initPlaylistInformation() {
        binding.textviewPlaylistName.text = playlist.name
        binding.textviewPlaylistDescription.text = playlist.description
        binding.textviewPlaylistTotalTime.text = getMinutePlural( totalTime.toLong())
        binding.textviewPlaylistTotalTracks.text = getTracksPlural(playlist.countOfTracksIn)
        loadTrackCoverInImageView()


    }
    private fun loadTrackCoverInImageView() {
        Log.d("load", playlist.coverUrl)
        Glide.with(binding.imageviewPlaylistCover)
            .load(playlist.coverUrl)
            .placeholder(R.drawable.img_track_placeholder)
            .error(R.drawable.img_track_placeholder)
            .transform(CenterCrop())
            .into(binding.imageviewPlaylistCover)
    }

    private fun initRvTracks() {
        binding.recyclerviewTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewTracks.adapter = TracksWithLongPressAdapter(
            tracks, onTrackPress, onTrackLongPress
        )
    }

    private fun initShareButton() {
        binding.buttonSharePlaylist.setOnClickListener {
            sharePlaylist()
        }
    }

    private fun initButtonSettings() {
        binding.buttonPlaylistSetting.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheetPlaylistSetting).state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initSettingBottomSheet() {
        binding.overlay.visibility = View.GONE

        initBottomSheet()
        initPlaylistRV()
        initButtonSettingShare()
        initButtonEditPlaylist()
        initButtonDeleteTrack()



    }

    private fun initBottomSheet() {
        val bottomSheet = BottomSheetBehavior.from(binding.bottomSheetPlaylistSetting).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isFitToContents=true
            skipCollapsed=true
        }
        bottomSheet.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // newState — новое состояние BottomSheet
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
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

    private fun initPlaylistRV() {
        binding.recyclerviewPlaylist.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewPlaylist.adapter = PlaylistHorizontalAdapter(playlistRv, {})
    }

    private fun initButtonSettingShare() {
        binding.buttonSettingShare.setOnClickListener {
            sharePlaylist()
        }
    }

    private fun initButtonEditPlaylist() {
        binding.buttonEditPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_addPlaylistFragment,
                AddPlaylistFragment.getArgs(playlist))
        }
    }

    private fun initButtonDeleteTrack() {
        binding.buttonDeletePlaylist.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheetPlaylistSetting).state = BottomSheetBehavior.STATE_HIDDEN
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("${getText(R.string.delete_playlist_question)} ${playlist.name}?")
                .setNegativeButton(R.string.no) { dialog, which ->
                    dialog.dismiss()
                }.setPositiveButton(R.string.yes) {dialog, which ->
                    viewModel.deletePlaylist()
                }.show()
        }
    }


    private fun observeDataFromViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistData.collect { state ->
                    when (state) {
                        is PlaylistDataViewModelState.Success -> {
                            // Обновляем данные плейлиста
                            playlist = state.playlist
                            playlistRv.clear()
                            playlistRv.add(playlist)
                            binding.recyclerviewPlaylist.adapter?.notifyDataSetChanged()


                            tracks.clear()
                            tracks.addAll(state.tracks)
                            totalTime = state.totalTime


                            initPlaylistInformation()
                            binding.recyclerviewTracks.adapter?.notifyDataSetChanged()

                        }
                        is PlaylistDataViewModelState.ClearedState -> {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                        is PlaylistDataViewModelState.DefaultState -> {

                        }
                    }
                }
            }
        }
    }


    private fun sharePlaylist() {
        if (tracks.isEmpty()) {
            Toast
                .makeText(requireContext(), getString(R.string.no_share_tracks_in_playlist), Toast.LENGTH_SHORT)
                .show()
        } else initShareProcess()
    }

    private fun initShareProcess() {
        val stringBuilder = StringBuilder()
        stringBuilder.append("${playlist.name}\n")
        stringBuilder.append("${playlist.description}\n")
        stringBuilder.append("${getTracksPlural(playlist.countOfTracksIn)}\n")
        tracks.forEachIndexed { index, track ->
            stringBuilder.append("${index+1}. ${track.trackName} - ${track.trackName}(${track.trackTime})")
            if (index < tracks.lastIndex) {
                stringBuilder.append("\n")
            }
        }

        val message = stringBuilder.toString()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        val chooser = Intent.createChooser(shareIntent, getString(R.string.share_playlist))
        requireContext().startActivity(chooser)
    }


    companion object {

        val PLAYLIST_ID_BUNDLE_KEY = "PLAYLIST_ID_KEY"

        fun getArgs(playlistId: Long): Bundle {
            return Bundle().apply {
                putLong(PLAYLIST_ID_BUNDLE_KEY, playlistId)
            }
        }


    }
}