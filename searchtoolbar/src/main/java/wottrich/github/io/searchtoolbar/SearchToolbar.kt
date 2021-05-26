package wottrich.github.io.searchtoolbar

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 25/05/2021
 *
 * Copyright © 2021 AndroidSearchToolbar. All rights reserved.
 *
 */

class SearchToolbar : Toolbar {

    val query: String
        get() = expandedInputSearch.text.toString()

    val shouldCallOnBackPressed: Boolean
        get() = isInitialState()

    var searchMargin = 0f
        private set

    var recentSearchedQuery: String = ""
        private set

    var toolbarState: SearchToolbarState = SearchToolbarState.Initial
        private set(value) {
            field = value
            updateViewsFromToolbarState(value)
        }

    var listener: SearchToolbarListener? = null
        private set

    private val collapsedView: ConstraintLayout by lazy { findViewById(R.id.collapsedView) }
    private val collapsedHint: TextView by lazy { findViewById(R.id.collapsedHint) }
    private val collapsedBackButton: ImageButton by lazy { findViewById(R.id.collapsedBackButton) }
    private val collapsedSearchButton: ImageButton by lazy { findViewById(R.id.collapsedSearchButton) }

    private val collapsedResetSearch: ImageButton by lazy { findViewById(R.id.collapsedResetSearch) }
    private val expandedView: ConstraintLayout by lazy { findViewById(R.id.expandedView) }
    private val expandedInputSearch: EditText by lazy { findViewById(R.id.expandedInputSearch) }
    private val expandedBackButton: ImageButton by lazy { findViewById(R.id.expandedBackButton) }

    private val expandedClearTextButton: ImageButton by lazy { findViewById(R.id.expandedClearTextButton) }

    private var expandedInputHintText: String? = ""
    private var collapsedHintText: String? = ""

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        searchMargin = context.resources.getDimension(R.dimen.search_margin)
        isSaveEnabled = true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupListeners()
        setInputHint()
        setCollapsedHint()
    }

    override fun onSaveInstanceState(): Parcelable {
        val state = super.onSaveInstanceState()
        return SearchSavedState(state).apply {
            this.expandedHint = expandedInputHintText
            this.collapsedHint = collapsedHintText
            this.isInitialState = isInitialState()
            this.isSearchState = isSearchState()
            this.isResultState = isResultState()
            this.queryResult = (toolbarState as? SearchToolbarState.Result)?.query
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SearchSavedState) {
            super.onRestoreInstanceState(state.superState)
            setInputHint(state.expandedHint)
            setCollapsedHint(state.collapsedHint)
            val toolbarState = when {
                state.isInitialState -> SearchToolbarState.Initial
                state.isSearchState -> SearchToolbarState.Search
                state.isResultState -> {
                    val queryResult = state.queryResult.orEmpty()
                    recentSearchedQuery = queryResult
                    SearchToolbarState.Result(queryResult)
                }
                else -> null
            }

            toolbarState?.let {
                restoreToolbarState(it)
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun setSearchListener(listener: SearchToolbarListener) {
        this.listener = listener
    }

    fun expand(text: String) {
        if (isSearchState()) {
            return
        }
        updateToolbarState(SearchToolbarState.Search)
        handleExpandAnimation(
            collapsedView,
            expandedView,
            expandedInputSearch,
            text
        )
    }

    fun collapse() {
        if (isInitialState()) {
            return
        }

        if (!isResultState()) {
            updateToolbarState(SearchToolbarState.Initial)
        }

        handleCollapseAnimation(
            collapsedView,
            expandedView,
            expandedInputSearch
        )
    }

    fun setText(text: String) {
        expandedInputSearch.setText(text)
    }

    fun clearSearchText() {
        setText("")
        cleanRecentSearchedQuery()
    }

    fun onSearchQuery(query: String?) {
        if (!query.isNullOrEmpty()) {
            setRecentSearchedQuery(query)
            updateToolbarState(SearchToolbarState.Result(query))
        }
        collapse()
        listener?.onSearch(query)
    }

    fun setInputHint(text: String? = context.getString(R.string.search_default_hint)) {
        context?.let {
            expandedInputHintText = text
            expandedInputSearch.hint = text
        }
    }

    fun setCollapsedHint(text: String? = context.getString(R.string.search_default_hint)) {
        context?.let {
            collapsedHintText = text
            collapsedHint.text = text
        }
    }

    fun setRecentSearchedQuery(query: String) {
        this.recentSearchedQuery = query
    }

    fun cleanRecentSearchedQuery() {
        recentSearchedQuery = ""
    }

    private fun setupListeners() {
        collapsedView.setOnClickListener { onExpandToolbarClicked() }
        collapsedBackButton.setOnClickListener { onCollapsedBackButtonClicked() }
        collapsedSearchButton.setOnClickListener { onExpandToolbarClicked() }
        collapsedResetSearch.setOnClickListener { onCollapsedResetSearchClicked() }
        expandedBackButton.setOnClickListener { onExpandedBackButtonClicked() }
        expandedClearTextButton.setOnClickListener { clearSearchText() }
        expandedInputSearch.doAfterTextChanged(::searchAfterTextChange)
        expandedInputSearch.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchQuery(query)
                true
            } else false
        }
    }

    private fun searchAfterTextChange(editable: Editable?) {
        val query = editable.toString()
        expandedClearTextButton.isVisible = query.isNotEmpty()
        listener?.onQueryUpdate(query)
    }

    private fun restoreToolbarState(state: SearchToolbarState) {
        when (state) {
            SearchToolbarState.Initial -> collapse()
            SearchToolbarState.Search -> expand(recentSearchedQuery)
            is SearchToolbarState.Result -> {
                updateToolbarState(state)
                collapse()
            }
        }
        updateViewsFromToolbarState(state)
    }

    private fun updateViewsFromToolbarState(state: SearchToolbarState) {
        when (state) {
            is SearchToolbarState.Initial -> {
                collapsedSearchButton.visibility = View.VISIBLE
                collapsedResetSearch.visibility = View.GONE
                setCollapsedHint()
            }
            is SearchToolbarState.Result -> {
                collapsedSearchButton.visibility = View.GONE
                collapsedResetSearch.visibility = View.VISIBLE
                setCollapsedHint(state.query)
            }
            is SearchToolbarState.Search -> Unit
        }
    }

    internal fun updateToolbarState(state: SearchToolbarState) {
        listener?.onToolbarStateUpdate(toolbarState, state)
        toolbarState = state
    }

    private class SearchSavedState : BaseSavedState {
        var collapsedHint: String? = null
        var expandedHint: String? = null
        var isInitialState: Boolean = false
        var isSearchState: Boolean = false
        var isResultState: Boolean = false
        var queryResult: String? = null

        constructor(superState: Parcelable?) : super(superState)
        private constructor(`in`: Parcel) : super(`in`) {
            loadParcel(`in`)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private constructor(`in`: Parcel, classLoader: ClassLoader?) : super(`in`, classLoader) {
            loadParcel(`in`)
        }

        private fun loadParcel(`in`: Parcel) {
            `in`.apply {
                collapsedHint = readString()
                expandedHint = readString()
                isInitialState = readInt() == 1
                isSearchState = readInt() == 1
                isResultState = readInt() == 1
                queryResult = readString()
            }
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.apply {
                writeString(collapsedHint)
                writeString(expandedHint)
                writeInt(if (isInitialState) 1 else 0)
                writeInt(if (isSearchState) 1 else 0)
                writeInt(if (isResultState) 1 else 0)
                writeString(queryResult)
            }
        }

        companion object CREATOR : Parcelable.Creator<SearchSavedState> {
            override fun createFromParcel(parcel: Parcel): SearchSavedState {
                return createParcel(parcel)
            }

            override fun newArray(size: Int): Array<SearchSavedState?> {
                return arrayOfNulls(size)
            }

            private fun createParcel(parcel: Parcel): SearchSavedState =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) SearchSavedState(parcel, SearchSavedState.javaClass.classLoader) else SearchSavedState(parcel)
        }

    }

    companion object {
        const val ANIMATE_DURATION = 100L
        val EXPAND_VALUES = floatArrayOf(1.0f, 0.5f, 0.0f)
        val COLLAPSED_VALUES = floatArrayOf(0.0f, 0.5f, 1.0f)
    }

}