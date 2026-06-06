package com.example.playlistmarket


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarket.ITunesSearchAPI.ITunesSearchAPIWorker
import com.example.playlistmarket.ITunesSearchAPI.TrackInfoResponse
import com.example.playlistmarket.SharedPreferencesPack.SearchHistoryPreferencesWorker
import com.example.playlistmarket.TrackModel.Track
import com.example.playlistmarket.TrackModel.TrackAdapter
import com.example.playlistmarket.utils.toTrackModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder


class SearchActivity : AppCompatActivity() {

    private var inputTextSearch: String = DEFAULT_TEXT_FOR_SEARCH
    private val tracks = mutableListOf<Track>()
    private val viewedTracks = SearchHistoryPreferencesWorker.viewedTracks.toMutableList()
    val onSearchHistoryChangeListener = SharedPreferences.OnSharedPreferenceChangeListener {
        ref, key ->
        updateViewedTracks()
    }
    private val itunesWorker = ITunesSearchAPIWorker()
    private val onSuccessLoadTrackInfo = ITunesSearchAPIWorker.OnResponseReactable { tracks ->
        updateTrackViewModel(tracks)
        if (this.tracks.isEmpty()) {
            showEmptyResultPlaceholder()
        }
    }
    private val onFailLoadTrackInfo = ITunesSearchAPIWorker.OnFailureReactable {
        showFailPlaceholder()
    }


    private lateinit var rvTracks : RecyclerView
    private lateinit var headerToolbar : MaterialToolbar
    private lateinit var searchTextInput : TextInputLayout
    private lateinit var searchEditText : TextInputEditText
    private lateinit var emptyResultPlaceholder : LinearLayout
    private lateinit var failLoadPlaceholder : LinearLayout
    private lateinit var buttonTryAgain : MaterialButton
    private lateinit var rvViewedTracks : RecyclerView
    private lateinit var layoutSearchHistory: LinearLayout
    private lateinit var buttonClearHistory: MaterialButton



    private fun updateTrackViewModel(itunesTracks: TrackInfoResponse?)  {
        tracks.clear()
        itunesTracks?.results?.mapTo(tracks) { itunesTrack ->
            itunesTrack.toTrackModel(this)
        }
        rvTracks.adapter?.notifyDataSetChanged()
    }

    private fun showEmptyResultPlaceholder() {
        emptyResultPlaceholder.visibility = View.VISIBLE
    }

    private fun showFailPlaceholder() {
        failLoadPlaceholder.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

        initAllPlaceholders()
        initRvTracks()
        initHeaderToolBar()
        initButtonTryAgain()
        initSearchTextInput()
        initLayoutSearchHistory()
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

    private fun initLayoutSearchHistory() {
        layoutSearchHistory = findViewById(R.id.layout_search_history)
        layoutSearchHistory.visibility = View.GONE
    }

    private fun initRvViewedTracks() {
        rvViewedTracks = findViewById(R.id.rv_viewed_tracks)
        rvViewedTracks.adapter = TrackAdapter(viewedTracks, this::onTrackClick)
        rvViewedTracks.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            true
        ).apply {
            stackFromEnd = true
        }
    }

    private fun addViewedTrack(track: Track) {
        SearchHistoryPreferencesWorker.addTrack(track)
    }

    private fun initButtonClearHistory() {
        buttonClearHistory = findViewById(R.id.button_clear_history)
        buttonClearHistory.setOnClickListener {
            SearchHistoryPreferencesWorker.clearSearchTrackHistory()
        }
    }

    private fun executeHistorySearchLogic(currentSearchText: String = searchEditText.text.toString()) {
        if (searchEditText.hasFocus() && currentSearchText.isEmpty() && !viewedTracks.isEmpty()) {
            layoutSearchHistory.visibility = View.VISIBLE
        } else {
            layoutSearchHistory.visibility = View.GONE
        }
    }

    fun initAllPlaceholders() {
        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        failLoadPlaceholder = findViewById(R.id.fail_load_placeholder)
        emptyResultPlaceholder.visibility = View.GONE
        failLoadPlaceholder.visibility = View.GONE
    }

    fun initRvTracks() {
        rvTracks = findViewById(R.id.rv_tracks)
        rvTracks.adapter = TrackAdapter(tracks, this::onTrackClick)
    }

    private fun onTrackClick(track: Track) {
        addViewedTrack(track)
        startMusicPlayer(track)
    }

    private fun startMusicPlayer(track: Track) {
        val musicPlayerIntent = Intent(this@SearchActivity, MusicPlayActivity::class.java)
        musicPlayerIntent.putExtra(
            MusicPlayActivity.JSON_FORMAT_TRACK_KEY,
            Gson().toJson(track)
        )
        startActivity(musicPlayerIntent)
    }

    fun initHeaderToolBar() {
        headerToolbar = findViewById(R.id.headerToolbar)
        headerToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun initButtonTryAgain() {
        buttonTryAgain = findViewById(R.id.button_try_again)
        buttonTryAgain.setOnClickListener { view ->
            loadTrackInfo()
        }
    }

    fun initSearchTextInput() {
        searchTextInput = findViewById(R.id.searchTextInput)
        searchEditText = findViewById(R.id.searchEditText)

        searchTextInput.setEndIconOnClickListener {
            resetAllSearchItems()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadTrackInfo()
                true
            } else {
                false
            }
        }

        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            executeHistorySearchLogic()
        }


        searchEditText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    inputTextSearch = searchEditText.text.toString().trim()
                    if (inputTextSearch.isEmpty()) {
                        resetViewModel()
                    }
                }
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    executeHistorySearchLogic(s.toString())
                }

            })
    }

    fun loadTrackInfo() {
        resetViewModel()
        resetAllPlaceholders()
        itunesWorker.getData(
            inputTextSearch,
            onSuccessLoadTrackInfo,
            onFailLoadTrackInfo)
    }

    fun resetAllSearchItems() {
        clearEditText()
        resetAllPlaceholders()
        resetViewModel()
    }

    fun resetAllPlaceholders() {
        emptyResultPlaceholder.visibility = View.GONE
        failLoadPlaceholder.visibility = View.GONE
        layoutSearchHistory.visibility = View.GONE
    }

    fun resetViewModel() {
        updateTrackViewModel(null)
    }

    fun clearEditText() {
        searchEditText.text?.clear()
        searchTextInput.clearFocus()
    }

    fun loadSearchStringInTextInput(searchString: String) {
        searchEditText.setText(searchString)
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

    private fun updateViewedTracks() {
        viewedTracks.clear()
        viewedTracks.addAll(
            SearchHistoryPreferencesWorker.viewedTracks.toMutableList()
        )
        rvViewedTracks.adapter?.notifyDataSetChanged()
        executeHistorySearchLogic()
    }

    override fun onStart() {
        super.onStart()
        SearchHistoryPreferencesWorker.registerListener(onSearchHistoryChangeListener)
        updateViewedTracks()
    }

    override fun onStop() {
        super.onStop()
        SearchHistoryPreferencesWorker.unregisterListener(onSearchHistoryChangeListener)
    }

    companion object {
        const val INPUT_TEXT_FOR_SEARCH : String = "INPUT_TEXT_FOR_SEARCH"
        const val DEFAULT_TEXT_FOR_SEARCH: String = ""

    }
}