package com.example.playlistmarket.ui.searchScreen


import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarket.Creator
import com.example.playlistmarket.R
import com.example.playlistmarket.data.network.ITunesSearchAPI.ITunesSearchApiTracksNetworkClient
import com.example.playlistmarket.domain.api.OnChangesRegisterable
import com.example.playlistmarket.domain.api.TracksInteractor
import com.example.playlistmarket.domain.models.Track
import com.example.playlistmarket.ui.musicPlayerScreen.MusicPlayActivity
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {



    private var tracksInteractor = Creator.provideTracksInteractor()
    private var onTrackClickAllowed = true
    private var showedUIStatus: ShowedUIStatus? = null
        get() = field
        set(value) {
            hideAllLogicElements()
            when (value) {
                ShowedUIStatus.TRACKS_FROM_SEARCH_UI -> showSearchTrackUI()
                ShowedUIStatus.ERROR_UI -> showFailPlaceholder()
                ShowedUIStatus.EMPTY_RESULT_UI -> showEmptyResultPlaceholder()
                ShowedUIStatus.LOAD_TRACKS_INFO_UI -> showLoadProcessBar()
                ShowedUIStatus.TRACKS_FROM_HISTORY_UI -> showTracksFromHistoryUI()
                ShowedUIStatus.All_LOGIC_UI_HIDED -> hideAllLogicElements()
                else -> hideAllLogicElements()

            }
            field = value
        }

    private var mainHandler = Handler(Looper.getMainLooper())
    private var inputTextSearch: String = DEFAULT_TEXT_FOR_SEARCH
    private var tracks = mutableListOf<Track>()
    private val viewedTracks = mutableListOf<Track>()
        //TracksHistoryPreferencesWorker.viewedTracks.toMutableList()
    val onSearchHistoryChangeListener = OnChangesRegisterable.Listener {
            mainHandler.post { updateViewedTracks() }
        }

    private val itunesWorker = ITunesSearchApiTracksNetworkClient()
    private val onSuccessLoadTrackInfo = TracksInteractor.TracksConsumer {
        tracksFromDomain ->
        mainHandler.post {
            Log.d("chuita", "Функция searchTracks вызвана с поисковым запросом:")
            updateTrackViewModel(tracksFromDomain)
            if (this.tracks.isEmpty()) {
                showedUIStatus = ShowedUIStatus.EMPTY_RESULT_UI
            }
        }

    }

    private val onFailLoadTrackInfo = ITunesSearchApiTracksNetworkClient.OnFailureReactable {
        showedUIStatus = ShowedUIStatus.ERROR_UI
    }

    private val searchTracksInfoRunnable = Runnable { loadTrackInfo() }


    private lateinit var rvTracks : RecyclerView
    private lateinit var progressBarLoadTracksInfo : ProgressBar
    private lateinit var headerToolbar : MaterialToolbar
    private lateinit var searchTextInput : TextInputLayout
    private lateinit var searchEditText : TextInputEditText
    private lateinit var emptyResultPlaceholder : LinearLayout
    private lateinit var failLoadPlaceholder : LinearLayout
    private lateinit var buttonTryAgain : MaterialButton
    private lateinit var rvViewedTracks : RecyclerView
    private lateinit var layoutSearchHistory: LinearLayout
    private lateinit var buttonClearHistory: MaterialButton



    private fun updateTrackViewModel(tracksFromDomain: List<Track>)  {
        tracks.clear()
        tracks.addAll(tracksFromDomain)
        rvTracks.adapter?.notifyDataSetChanged()
        showedUIStatus = ShowedUIStatus.TRACKS_FROM_SEARCH_UI
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
        initProgressBarLoadTracksInfo()
        if (savedInstanceState != null) {
            loadSearchStringInTextInput(
                savedInstanceState.getString(
                    INPUT_TEXT_FOR_SEARCH,
                    DEFAULT_TEXT_FOR_SEARCH)
            )
        }

        showedUIStatus = ShowedUIStatus.All_LOGIC_UI_HIDED



    }

    private fun initProgressBarLoadTracksInfo() {
        progressBarLoadTracksInfo = findViewById(R.id.progressBarLoadTracksInfo)
    }

    private fun initLayoutSearchHistory() {
        layoutSearchHistory = findViewById(R.id.layout_search_history)
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
        tracksInteractor.insertTrackInTracksHistory(track)
    }

    private fun initButtonClearHistory() {
        buttonClearHistory = findViewById(R.id.button_clear_history)
        buttonClearHistory.setOnClickListener {
            tracksInteractor.clearTracksHistory()
        }
    }

    private fun executeHistorySearchLogic(currentSearchText: String = searchEditText.text.toString()) {
        if (searchEditText.hasFocus() && currentSearchText.isEmpty() && !viewedTracks.isEmpty()) {
            showedUIStatus = ShowedUIStatus.TRACKS_FROM_HISTORY_UI
        } else {
            showedUIStatus = ShowedUIStatus.TRACKS_FROM_SEARCH_UI
        }
    }

    fun initAllPlaceholders() {
        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        failLoadPlaceholder = findViewById(R.id.fail_load_placeholder)
    }

    fun initRvTracks() {
        rvTracks = findViewById(R.id.rv_tracks)
        rvTracks.adapter = TrackAdapter(tracks, this::onTrackClick)
    }

    private fun onTrackClick(track: Track) {
        if (onTrackClickDebounce()) {
            addViewedTrack(track)
            startMusicPlayer(track)
        }
    }

    private fun startMusicPlayer(track: Track) {
        val musicPlayerIntent = Intent(this@SearchActivity, MusicPlayActivity::class.java)
        musicPlayerIntent.putExtra(
            MusicPlayActivity.Companion.JSON_FORMAT_TRACK_KEY,
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
                    mainHandler.removeCallbacks(searchTracksInfoRunnable)
                    inputTextSearch = searchEditText.text.toString().trim()
                    if (inputTextSearch.isEmpty()) {
                        resetViewModel()
                        executeHistorySearchLogic()
                    } else {
                        mainHandler.postDelayed(
                            searchTracksInfoRunnable,
                            DEBOUNCE_MILLISECOND_TIME_TO_MAKE_REQUEST
                        )
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
        showedUIStatus = ShowedUIStatus.LOAD_TRACKS_INFO_UI
        tracksInteractor.searchTracks(
            inputTextSearch,
            onSuccessLoadTrackInfo,
            {
                mainHandler.post { showedUIStatus = ShowedUIStatus.ERROR_UI }
            }
        )
    }

    fun resetAllSearchItems() {
        clearEditText()
        showedUIStatus = ShowedUIStatus.All_LOGIC_UI_HIDED
        resetViewModel()
    }

    fun hideAllLogicElements() {
        emptyResultPlaceholder.visibility = View.GONE
        failLoadPlaceholder.visibility = View.GONE
        layoutSearchHistory.visibility = View.GONE
        rvTracks.visibility = View.GONE
        progressBarLoadTracksInfo.visibility = View.GONE
    }

    fun resetViewModel() {
        updateTrackViewModel(emptyList())
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
        tracksInteractor.getTracksHistory{ tracks ->
            mainHandler.post {
                viewedTracks.clear()
                viewedTracks.addAll(tracks)
                rvViewedTracks.adapter?.notifyDataSetChanged()
                executeHistorySearchLogic()
            }
        }
    }

    private fun showTracksFromHistoryUI() {
        layoutSearchHistory.visibility = View.VISIBLE
    }

    private fun showSearchTrackUI() {
        rvTracks.visibility = View.VISIBLE
    }

    private fun showLoadProcessBar() {
        progressBarLoadTracksInfo.visibility = View.VISIBLE
    }
    override fun onStart() {
        super.onStart()
        tracksInteractor.registerOnChanges(onSearchHistoryChangeListener)
        updateViewedTracks()
    }

    override fun onStop() {
        super.onStop()
        tracksInteractor.unregisterOnChanges(onSearchHistoryChangeListener)
    }

    fun onTrackClickDebounce(): Boolean {
        val current = onTrackClickAllowed
        if (onTrackClickAllowed) {

            onTrackClickAllowed = false
            mainHandler.postDelayed({onTrackClickAllowed = true}, DEBOUNCE_MILLISECOND_TIME_TO_CLICK_ON_TRACK)

        }
        return current
    }

    companion object {
        const val INPUT_TEXT_FOR_SEARCH : String = "INPUT_TEXT_FOR_SEARCH"
        const val DEFAULT_TEXT_FOR_SEARCH: String = ""

        const val DEBOUNCE_MILLISECOND_TIME_TO_MAKE_REQUEST: Long = 2000

        const val DEBOUNCE_MILLISECOND_TIME_TO_CLICK_ON_TRACK: Long = 1000

    }

    private enum class ShowedUIStatus {
        TRACKS_FROM_SEARCH_UI,
        TRACKS_FROM_HISTORY_UI,
        EMPTY_RESULT_UI,
        ERROR_UI,
        LOAD_TRACKS_INFO_UI,
        All_LOGIC_UI_HIDED
    }
}