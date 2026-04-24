package com.focus.mob.utils
import com.focus.mob.R

import android.app.Activity

/**
 * Extension function to safely perform a fade transition between activities,
 * wrapping the deprecated overridePendingTransition for cleaner code.
 */
@Suppress("DEPRECATION")
fun Activity.fadeTransition() {
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}
