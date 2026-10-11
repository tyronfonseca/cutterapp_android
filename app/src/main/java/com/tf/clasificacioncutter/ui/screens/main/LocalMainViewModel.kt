package com.tf.clasificacioncutter.ui.screens.main

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal used to provide MainViewModel down the UI tree implicitly.
 */
val LocalMainViewModel = compositionLocalOf<MainViewModel> {
    error("No MainViewModel provided in CompositionLocalProvider")
}