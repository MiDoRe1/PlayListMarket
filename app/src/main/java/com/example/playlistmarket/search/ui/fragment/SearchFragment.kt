package com.example.playlistmarket.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmarket.R
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmarket.core.ui.BindingFragment
import com.example.playlistmarket.databinding.FragmentSearchBinding
import com.example.playlistmarket.player.ui.fragment.MusicPlayerFragment
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.search.ui.viewmodel.SearchViewModel
import com.example.playlistmarket.search.ui.viewmodel.State
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    private  val viewModel: SearchViewModel by viewModel()

    private var inputTextSearch: String = DEFAULT_TEXT_FOR_SEARCH
    private var tracks = mutableListOf<Track>()
    private val viewedTracks = mutableListOf<Track>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel.observeStateViewModel().observe(viewLifecycleOwner){
            hideAllLogicElements()
            when (it) {
                is State.FoundTracksState -> {
                    updateTracks(it.tracks)
                    showSearchTrackUI()
                }
                is State.DefaultState -> {
                    hideAllLogicElements()
                }
                is State.HistoryState -> {
                    updateViewedTracks(it.tracks)
                    showTracksFromHistoryUI()
                }
                is State.LoadingState -> {
                    showLoadProcessBar()
                }
                is State.EmptyResultState -> {
                    binding.emptyResultPlaceholder.visibility = View.VISIBLE
                }
                is State.ErrorState -> {
                    binding.failLoadPlaceholder.visibility = View.VISIBLE
                }
            }
        }

        initFailLoadPlaceHolder()
        initEmptyResultPlaceholder()
        initRvTracks()
        initButtonTryAgain()
        initSearchTextInput()

        initRvViewedTracks()
        initButtonClearHistory()


        if (savedInstanceState != null) {
            loadSearchStringInTextInput(
                savedInstanceState.getString(
                    INPUT_TEXT_FOR_SEARCH,
                    DEFAULT_TEXT_FOR_SEARCH)
            )
        }
    }


    private fun initRvViewedTracks() {

        binding.rvViewedTracks.adapter = TrackAdapter(viewedTracks) { track ->
            viewModel.insertTrackInHistory(track)
            findNavController().navigate(
                R.id.action_searchFragment_to_musicPlayerFragment,
                MusicPlayerFragment.getArgs(Gson().toJson(track))
            )
        }

        binding.rvViewedTracks.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initButtonClearHistory() {
        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    fun initEmptyResultPlaceholder() {}

    fun initFailLoadPlaceHolder() {}


    fun initRvTracks() {
        binding.rvTracks.adapter = TrackAdapter(tracks) { track ->
            viewModel.insertTrackInHistory(track)
            findNavController().navigate(
                R.id.action_searchFragment_to_musicPlayerFragment,
                MusicPlayerFragment.getArgs(Gson().toJson(track))
            )
        }
    }


    fun initButtonTryAgain() {
        binding.buttonTryAgain.setOnClickListener { _ ->
            viewModel.findTracks(inputTextSearch)
        }
    }

    fun initSearchTextInput() {
        binding.searchTextInput.setEndIconOnClickListener {
            binding.searchEditText.setText("")
            binding.searchEditText.clearFocus()
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus == true) {
                inputTextSearch = binding.searchEditText.text.toString().trim()
                viewModel.findTracks(inputTextSearch)
            }
        }

        binding.searchEditText.addTextChangedListener {
            inputTextSearch = binding.searchEditText.text.toString().trim()
            viewModel.findTracks(inputTextSearch)
        }

    }

    fun hideAllLogicElements() {
        binding.emptyResultPlaceholder.visibility = View.GONE
        binding.failLoadPlaceholder.visibility = View.GONE
        binding.layoutSearchHistory.visibility = View.GONE
        binding.frameLayoutSearchPlaceholder.visibility = View.GONE
        binding.frameLayoutLoadingPlaceholder.visibility = View.GONE
    }

    fun loadSearchStringInTextInput(searchString: String) {
        binding.searchEditText.setText(searchString)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT_FOR_SEARCH ,inputTextSearch)
    }

    private fun updateViewedTracks(tracks: List<Track>) {
        viewedTracks.clear()
        viewedTracks.addAll(tracks)
        binding.rvViewedTracks.adapter?.notifyDataSetChanged()
    }

    private fun showTracksFromHistoryUI() {
        binding.layoutSearchHistory.visibility = View.VISIBLE
    }

    private fun showSearchTrackUI() {
        binding.frameLayoutSearchPlaceholder.visibility = View.VISIBLE
    }

    private fun showLoadProcessBar() {
        binding.frameLayoutLoadingPlaceholder.visibility = View.VISIBLE
    }

    private fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        binding.rvTracks.adapter?.notifyDataSetChanged()
    }

    companion object {
        const val INPUT_TEXT_FOR_SEARCH : String = "INPUT_TEXT_FOR_SEARCH"
        const val DEFAULT_TEXT_FOR_SEARCH: String = ""

    }
}