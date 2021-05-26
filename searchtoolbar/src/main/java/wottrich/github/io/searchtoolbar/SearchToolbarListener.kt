package wottrich.github.io.searchtoolbar

interface SearchToolbarListener {

    /**
     * [onExpandToolbar] is called when toolbar user clicks to search
     * [onExpandToolbar] represents [SearchToolbarState.Search] state
     *
     * @param query - the text showed in toolbar input
     */
    fun onExpandToolbar(query: String?)

    /**
     * [onCollapseToolbar] is called when user requests or cancel a searching
     * [onCollapseToolbar] represents [SearchToolbarState.Initial] state
     */
    fun onCollapseToolbar()

    /**
     * [onHomeButtonClicked] is called when home button was clicked
     * [onHomeButtonClicked] will be called in two situations
     * when the state is [SearchToolbarState.Initial] or [SearchToolbarState.Search]
     *
     * Both states was chosen to we can configure onBackPressed action as needed
     */
    fun onHomeButtonClicked()

    /**
     * [onSearch] is called when some search is requested
     * [onSearch] represents [SearchToolbarState.Result] state
     *
     * @param query - Text requested to search
     */
    fun onSearch(query: String?)

    /**
     * [onToolbarStateUpdate] is called when any [SearchToolbarState] changes
     *
     * @param oldState - State before change to new state
     * @param currentState - Current state of [SearchToolbar]
     */
    fun onToolbarStateUpdate(oldState: SearchToolbarState, currentState: SearchToolbarState)

    /**
     * [onQueryUpdate] is called always that have some change in input text
     *
     * @param query - Typed text in the input
     */
    fun onQueryUpdate(query: String?)

}