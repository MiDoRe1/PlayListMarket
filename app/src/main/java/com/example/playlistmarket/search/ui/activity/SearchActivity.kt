package com.example.playlistmarket.search.ui.activity



import com.google.android.material.textfield.TextInputEditText
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmarket.R
import com.example.playlistmarket.databinding.ActivitySearchBinding
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.search.ui.viewmodel.SearchViewModel
import com.example.playlistmarket.search.ui.viewmodel.State

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: ActivitySearchBinding
    private var inputTextSearch: String = DEFAULT_TEXT_FOR_SEARCH
    private var tracks = mutableListOf<Track>()
    private val viewedTracks = mutableListOf<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setPadding(
                v.paddingLeft,
                statusBar.top,
                v.paddingRight,
                navigationBars.bottom
            )
            insets
        }

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())
            .get(SearchViewModel::class.java)

        viewModel.observeStateViewModel().observe(this){
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
        initHeaderToolBar()
        initButtonTryAgain()
        initSearchTextInput()
        initLayoutSearchHistory()
        initRvViewedTracks()
        initButtonClearHistory()
        initProgressBarLoadTracksInfo()

        if (savedInstanceState != null) {
            loadSearchStringInTextInput(
                savedInstanceState.getString(
                    INPUT_TEXT_FOR_SEARCH,
                    DEFAULT_TEXT_FOR_SEARCH)
            )
        }
    }

    private fun initProgressBarLoadTracksInfo() {}

    private fun initLayoutSearchHistory() {}

    private fun initRvViewedTracks() {
        binding.rvViewedTracks.adapter = TrackAdapter(viewedTracks) {
            track ->
            viewModel.openMusicPlayer(track, this.applicationContext)
        }
        binding.rvViewedTracks.layoutManager = LinearLayoutManager(this)
    }

    private fun initButtonClearHistory() {
        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    fun initEmptyResultPlaceholder() {}

    fun initFailLoadPlaceHolder() {}


    fun initRvTracks() {
        binding.rvTracks.adapter = TrackAdapter(tracks) {
            track ->
            viewModel.openMusicPlayer(track, this.applicationContext)
        }
    }


    fun initHeaderToolBar() {
        binding.headerToolbar.setNavigationOnClickListener {
            finish()
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
        binding.rvTracks.visibility = View.GONE
        binding.progressBarLoadTracksInfo.visibility = View.GONE
    }

    fun loadSearchStringInTextInput(searchString: String) {
        binding.searchEditText.setText(searchString)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT_FOR_SEARCH ,inputTextSearch)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        val searchEditText = findViewById<TextInputEditText>(R.id.searchEditText)
        if (savedInstanceState != null) {
            val searchTextFromBundle = savedInstanceState.getString(
                INPUT_TEXT_FOR_SEARCH,
                DEFAULT_TEXT_FOR_SEARCH)
            inputTextSearch = searchTextFromBundle
            searchEditText.setText(inputTextSearch)
        }
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
        binding.rvTracks.visibility = View.VISIBLE
    }

    private fun showLoadProcessBar() {
        binding.progressBarLoadTracksInfo.visibility = View.VISIBLE
    }

    private fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        binding.rvTracks.adapter?.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    companion object {
        const val INPUT_TEXT_FOR_SEARCH : String = "INPUT_TEXT_FOR_SEARCH"
        const val DEFAULT_TEXT_FOR_SEARCH: String = ""

    }


}