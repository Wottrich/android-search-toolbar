package wottrich.github.io.searchtoolbar

sealed class SearchToolbarState {
    object Initial : SearchToolbarState()
    object Search : SearchToolbarState()
    data class Result(val query: String) : SearchToolbarState()
}