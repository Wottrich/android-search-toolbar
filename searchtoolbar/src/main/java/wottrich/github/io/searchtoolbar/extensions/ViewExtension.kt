package wottrich.github.io.searchtoolbar.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 26/05/2021
 *
 * Copyright © 2021 AndroidSearchToolbar. All rights reserved.
 *
 */

fun View.crossfade(viewToShow: View, duration: Long = 400, block: (AnimatorSet.() -> Unit)? = null): AnimatorSet {
    viewToShow.alpha = 0f
    viewToShow.visibility = View.VISIBLE
    val out = alphaAnimator(1f, 0f) {
        doOnEnd {
            visibility = View.INVISIBLE
        }
    }
    val `in` = viewToShow.alphaAnimator(0f, 1f)
    return AnimatorSet().apply {
        playTogether(out, `in`)
        this.duration = duration
        interpolator = AccelerateDecelerateInterpolator()
        block?.invoke(this)
    }
}

fun View.alphaAnimator(vararg values: Float, duration: Long = 200L, block: (ObjectAnimator.() -> Unit)? = null): ValueAnimator {
    return ObjectAnimator.ofFloat(this, "alpha", *values).apply {
        this.duration = duration
        this.interpolator = AccelerateInterpolator()
        block?.invoke(this)
    }
}