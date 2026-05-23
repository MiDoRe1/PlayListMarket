package com.example.playlistmarket

import android.health.connect.datatypes.units.Length
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.example.playlistmarket.ITunesSearchAPI.ITunesSearchAPIWorker
import com.example.playlistmarket.ITunesSearchAPI.TrackInfo
import com.example.playlistmarket.ITunesSearchAPI.TrackInfoResponse
import com.example.playlistmarket.TrackModel.Track
import com.example.playlistmarket.TrackModel.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private var inputTextSearch: String = DEFAULT_TEXT_FOR_SEARCH
    private val tracks = mutableListOf<Track>()
    private val itunesWorker = ITunesSearchAPIWorker()
    private val onSuccessLoadTrackInfo = ITunesSearchAPIWorker.OnResponseReactable { tracks ->
        updateTrackViewModel(tracks)
        if (this.tracks.size == 0) {
            searchEditText.setText("")
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


    private fun updateTrackViewModel(itunesTracks: TrackInfoResponse?)  {
        tracks.clear()
        val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

        itunesTracks?.results?.mapTo(tracks) { itunesTrack ->
            Track(
                trackName = itunesTrack.trackName ?: getString(R.string.unknown_track_name),
                artistName = itunesTrack.artistName ?: getString(R.string.unknown_artist_name),
                trackTime = timeFormatter.format(itunesTrack.trackTimeMillis ?: 0L),
                artworkUrl100 = itunesTrack.artworkUrl100 ?: getString(R.string.unknown_track_url)
            )
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
            v.updatePadding(top = statusBar.top)
            insets
        }

        initAllPlaceholders()
        initRvTracks()
        initHeaderToolBar()
        initButtonTryAgain()
        initSearchTextInput()
        if (savedInstanceState != null) {
            loadSearchStringInTextInput(
                savedInstanceState.getString(
                    INPUT_TEXT_FOR_SEARCH,
                    DEFAULT_TEXT_FOR_SEARCH)
            )
        }



    }

    fun initAllPlaceholders() {
        emptyResultPlaceholder = findViewById<LinearLayout>(R.id.empty_result_placeholder)
        failLoadPlaceholder = findViewById<LinearLayout>(R.id.fail_load_placeholder)
        emptyResultPlaceholder.visibility = View.GONE
        failLoadPlaceholder.visibility = View.GONE
    }

    fun initRvTracks() {
        rvTracks = findViewById<RecyclerView>(R.id.rv_tracks)
        rvTracks.adapter = TrackAdapter(tracks)
    }

    fun initHeaderToolBar() {
        headerToolbar = findViewById<MaterialToolbar>(R.id.headerToolbar)
        headerToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun initButtonTryAgain() {
        buttonTryAgain = findViewById<MaterialButton>(R.id.button_try_again)
        buttonTryAgain.setOnClickListener { view ->
            loadTrackInfo()
        }
    }

    fun initSearchTextInput() {
        searchTextInput = findViewById<TextInputLayout>(R.id.searchTextInput)
        searchEditText = findViewById<TextInputEditText>(R.id.searchEditText)

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

        searchEditText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    inputTextSearch = searchEditText.text.toString().trim()
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

    companion object {
        val INPUT_TEXT_FOR_SEARCH : String = "INPUT_TEXT_FOR_SEARCH"
        val DEFAULT_TEXT_FOR_SEARCH: String = ""

    }
}