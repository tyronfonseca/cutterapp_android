package com.tf.clasificacioncutter.utils.extensions

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap

/**
 * Remembers a [Painter] for the given drawable resource ID.
 * Supports all Android drawable types including AdaptiveIcons (<adaptive-icon>),
 * VectorDrawables (<vector>), selectors (<selector>), shapes (<shape>), and rasterized images (PNG/JPG/WEBP).
 */
@Composable
fun rememberDrawablePainter(@DrawableRes id: Int): Painter {
    val context = LocalContext.current
    return remember(id, context) {
        val drawable = AppCompatResources.getDrawable(context, id)
        if (drawable != null) {
            if (drawable is BitmapDrawable && drawable.bitmap != null) {
                BitmapPainter(drawable.bitmap.asImageBitmap())
            } else {
                val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 512
                val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 512
                val bitmap = createBitmap(width, height)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                BitmapPainter(bitmap.asImageBitmap())
            }
        } else {
            BitmapPainter(ImageBitmap(1, 1))
        }
    }
}
