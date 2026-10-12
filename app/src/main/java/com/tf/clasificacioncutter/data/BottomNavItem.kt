package com.tf.clasificacioncutter.data

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val route: String,
)
