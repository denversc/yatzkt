package com.yatzkt.accessibility

import android.content.Context
import android.provider.Settings

object AccessibilityUtils {
    /**
     * Determines whether reduced motion/animations are requested by the user in system settings.
     * If any animation scale is 0, animations and non-essential haptics should be suppressed.
     * If accessibility settings cannot be queried, defaults to false (animations enabled).
     */
    fun isReducedMotionEnabled(context: Context): Boolean {
        return try {
            val resolver = context.contentResolver
            val animatorScale = Settings.Global.getFloat(
                resolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            val transitionScale = Settings.Global.getFloat(
                resolver,
                Settings.Global.TRANSITION_ANIMATION_SCALE,
                1.0f
            )
            animatorScale == 0.0f || transitionScale == 0.0f
        } catch (_: Exception) {
            false
        }
    }
}
