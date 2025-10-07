package com.leanrada.easyqueasy.services

import android.content.Context
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams

fun addOverlayView(context: Context, view: View, layoutType: Int) {
    try {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        if (windowManager == null) {
            android.util.Log.e("AddOverlayView", "WindowManager is null")
            return
        }

        val layoutParams = LayoutParams()
        layoutParams.apply {
            width = LayoutParams.MATCH_PARENT
            height = LayoutParams.MATCH_PARENT
            format = PixelFormat.TRANSPARENT
            type = layoutType
            flags =
                LayoutParams.FLAG_NOT_TOUCHABLE or
                        LayoutParams.FLAG_NOT_FOCUSABLE or
                        LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        }

        windowManager.addView(view, layoutParams)
    } catch (e: Exception) {
        android.util.Log.e("AddOverlayView", "Failed to add overlay view", e)
        throw e
    }
}