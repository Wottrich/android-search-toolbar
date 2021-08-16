package wottrich.github.io.searchtoolbar.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 26/05/2021
 *
 * Copyright © 2021 AndroidSearchToolbar. All rights reserved.
 *
 */

fun Context.hideKeyboard(view: View?) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.applicationWindowToken, 0)
}

fun Context.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}