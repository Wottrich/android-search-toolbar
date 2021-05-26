package wottrich.github.io.searchtoolbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import wottrich.github.io.searchtoolbar.extensions.crossfade
import wottrich.github.io.searchtoolbar.extensions.hideKeyboard
import wottrich.github.io.searchtoolbar.extensions.showKeyboard

fun SearchToolbar.isInitialState() = toolbarState is SearchToolbarState.Initial
fun SearchToolbar.isSearchState() = toolbarState is SearchToolbarState.Search
fun SearchToolbar.isResultState() = toolbarState is SearchToolbarState.Result

fun SearchToolbar.onCollapsedBackButtonClicked() {
    if (isResultState()) {
        cleanRecentSearchedQuery()
        updateToolbarState(SearchToolbarState.Initial)
    } else {
        listener?.onHomeButtonClicked()
    }
}

fun SearchToolbar.onBackPressed() {
    if (isSearchState()) {
        onExpandedBackButtonClicked()
    } else {
        onCollapsedBackButtonClicked()
    }
}

internal fun SearchToolbar.handleExpandAnimation(
    collapsedView: View,
    expandedView: View,
    expandedInputSearch: EditText,
    text: String
) {
    collapsedView.crossfade(expandedView, SearchToolbar.ANIMATE_DURATION) {
        doOnStart {
            expandedView.visibility = View.VISIBLE
        }
        doOnEnd {
            collapsedView.visibility = View.GONE
        }
    }.start()
    createAnimator(SearchToolbar.EXPAND_VALUES, object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            context?.showKeyboard()
        }

        override fun onAnimationEnd(animation: Animator?) {
            setText(text)
            expandedInputSearch.requestFocus()
        }
    })?.start()
}

internal fun SearchToolbar.handleCollapseAnimation(
    collapsedView: View,
    expandedView: View,
    expandedInputSearch: EditText
) {
    expandedView.crossfade(collapsedView).start()
    createAnimator(SearchToolbar.COLLAPSED_VALUES, object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            context?.hideKeyboard(expandedInputSearch)
        }

        override fun onAnimationEnd(animation: Animator?) {
            expandedView.visibility = View.INVISIBLE
        }
    })?.start()
}

internal fun SearchToolbar.onExpandedBackButtonClicked() {
    if (recentSearchedQuery.isNotEmpty()) {
        updateToolbarState(SearchToolbarState.Result(recentSearchedQuery))
    }
    collapse()
    listener?.onCollapseToolbar()
}

internal fun SearchToolbar.onCollapsedResetSearchClicked() {
    cleanRecentSearchedQuery()
    expand("")
}

internal fun SearchToolbar.onExpandToolbarClicked() {
    expand(recentSearchedQuery)
    listener?.onExpandToolbar(recentSearchedQuery)
}

internal fun SearchToolbar.createAnimator(
    values: FloatArray,
    listener: AnimatorListenerAdapter
): ValueAnimator? =
    ValueAnimator.ofFloat(*values).apply {
        addUpdateListener(::onUpdateAnimateListener)
        addListener(listener)
        duration = SearchToolbar.ANIMATE_DURATION
    }

internal fun SearchToolbar.onUpdateAnimateListener(valueAnimator: ValueAnimator) {
    setMargins(valueAnimator.animatedValue as? Float)
}

internal fun SearchToolbar.setMargins(fraction: Float?) {
    val margin = (searchMargin * (fraction ?: 0f)).toInt()
    val params = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        topMargin = margin
        bottomMargin = margin
        leftMargin = margin
        rightMargin = margin
    }
    layoutParams = params
}
