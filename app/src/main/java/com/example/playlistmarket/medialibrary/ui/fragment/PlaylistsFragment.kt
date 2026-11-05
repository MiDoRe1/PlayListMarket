package com.example.playlistmarket.medialibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmarket.R
import com.example.playlistmarket.databinding.FragmentPlaylistsBinding
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.medialibrary.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmarket.medialibrary.ui.viewmodel.PlaylistsVmState
import com.example.playlistmarket.player.ui.fragment.MusicPlayerFragment
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    val binding: FragmentPlaylistsBinding get() = _binding!!

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    private val onItemClickCallBack = { playlist: Playlist ->
        playlistsViewModel.showPlaylistInfo(playlist)
    }


    private val playlists = mutableListOf<Playlist>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreateNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_addPlaylistFragment
            )
        }

        initRvPlaylist()

        playlistsViewModel.observeLiveDataVmState().observe(viewLifecycleOwner) { state ->
            hideAllPlaceholders()
            when (state) {
                is PlaylistsVmState.ShowPlaylistsState -> {
                    updatePlaylistsRv(state.playlists)
                    showPlaylists()
                }
                is PlaylistsVmState.EmptyResults -> showEmptyPlaceHolder()
            }
        }

        playlistsViewModel.getPlayLists()
    }

    private fun initButtonCreateNewPlaylist() {
        initButtonCreateNewPlaylist()
    }
    private fun initRvPlaylist() {
        binding.recyclerViewPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewPlaylists.adapter = PlaylistAdapter(playlists, onItemClickCallBack)
    }

    private fun hideAllPlaceholders() {
        binding.placeholderPlaylist.visibility = View.GONE
        binding.emptyPlaceholder.visibility = View.GONE
    }

    private fun updatePlaylistsRv(newList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newList)
        binding.recyclerViewPlaylists.adapter?.notifyDataSetChanged()
    }

    private fun showPlaylists() {
        binding.placeholderPlaylist.visibility = View.VISIBLE
    }

    private fun showEmptyPlaceHolder() {
        binding.emptyPlaceholder.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}