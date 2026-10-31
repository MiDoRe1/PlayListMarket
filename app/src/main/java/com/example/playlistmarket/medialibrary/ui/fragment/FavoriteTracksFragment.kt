package com.example.playlistmarket.medialibrary.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import com.example.playlistmarket.R
import com.example.playlistmarket.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmarket.medialibrary.ui.viewmodel.FavoriteTracksStates
import com.example.playlistmarket.medialibrary.ui.viewmodel.FavoriteTracksViewModel
import com.example.playlistmarket.medialibrary.ui.viewmodel.RequestToUIState
import com.example.playlistmarket.player.ui.fragment.MusicPlayerFragment
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.search.ui.fragment.TrackAdapter
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null

    private val favoriteTracks: MutableList<Track> = mutableListOf()

    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel()
    val binding: FragmentFavoriteTracksBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRVFavoriteTracks()

        favoriteTracksViewModel.observeStateViewModel().observe(viewLifecycleOwner) {
            value ->
            hideAllPlaceholders()
            when (value) {
                is FavoriteTracksStates.DefaultState -> {
                    hideAllPlaceholders()
                }
                is FavoriteTracksStates.ShowFavoriteTracks -> {
                    showFavoriteTracks(value.tracks)
                }
                is FavoriteTracksStates.EmptyResultState -> {
                    showEmptyPlaceholder()
                }
            }
        }
        favoriteTracksViewModel.observeSingleEventRequestToUIState().observe(viewLifecycleOwner) {
            value ->
            when(value) {
                is RequestToUIState.OpenMusicPlayerRequestState -> navigateToMusicPlayer(value.track)
            }
        }
        favoriteTracksViewModel.getFavoritesTracks()
    }

    private fun hideAllPlaceholders() {
        binding.placeholderFavoriteTracks.visibility = View.GONE
        binding.placeholderEmptyList.visibility = View.GONE
    }

    private fun showFavoriteTracks(tracks: List<Track>) {
        favoriteTracks.clear()
        favoriteTracks.addAll(tracks)
        binding.recyclerviewFavoriteTracks.adapter?.notifyDataSetChanged()
        binding.placeholderFavoriteTracks.visibility = View.VISIBLE
    }

    private fun showEmptyPlaceholder() {
        binding.placeholderEmptyList.visibility = View.VISIBLE
    }

    private fun navigateToMusicPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_musicPlayerFragment,
            MusicPlayerFragment.getArgs(Gson().toJson(track))
            )
    }

    fun initRVFavoriteTracks() {
        binding.recyclerviewFavoriteTracks.adapter = TrackAdapter(favoriteTracks) {
            track -> favoriteTracksViewModel.openMusicPlayer(track)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}