package com.tf.clasificacioncutter.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo

/**
 * Locks screen orientation to portrait on phone devices (smallest width < 600dp),
 * while allowing tablets (smallest width >= 600dp) to use landscape or sensor orientation.
 */
@SuppressLint("SourceLockedOrientationActivity")
fun Activity.lockPortraitOnPhones() {
    val smallestWidthDp = resources.configuration.smallestScreenWidthDp
    if (smallestWidthDp < 600) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
