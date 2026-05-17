package com.example.playlistmarket

import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SearchActivity : AppCompatActivity() {

    private var inputTextSearch: String = DEFAULT_TEXT_FOR_SEARCH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBar.top)
            insets
        }
        val headerToolbar = findViewById<MaterialToolbar>(R.id.headerToolbar)
        headerToolbar.setNavigationOnClickListener {
            finish()
        }

        val searchTextInput = findViewById<TextInputLayout>(R.id.searchTextInput)
        val searchEditText = findViewById<TextInputEditText>(R.id.searchEditText)

        searchTextInput.setEndIconOnClickListener {
            searchEditText.text?.clear()
            searchTextInput.clearFocus()
        }

        if (savedInstanceState != null) {
            val searchTextFromBundle = savedInstanceState.getString(
                INPUT_TEXT_FOR_SEARCH,
                DEFAULT_TEXT_FOR_SEARCH)
            searchEditText.setText(searchTextFromBundle)
        }

        searchEditText.addTextChangedListener(
            object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                if (searchEditText.text?.isEmpty() == false) {
                    inputTextSearch = searchEditText.text.toString()
                }
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