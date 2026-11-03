package com.backscreen.wallpaper.core

/** A display the phone has, as [RearDisplayPick] sees it: its id, size in pixels, and whether it's private to an app. */
data class DisplayCandidate(val id: Int, val width: Int, val height: Int, val isPrivate: Boolean = false)

/**
 * Which display is the back screen. Not simply the first "presentation" display, as before:
 * Android lists a cast screen, a USB-C monitor or a simulated display ahead of the phone's own
 * (DisplayManager.getDisplays), so one of those would have got the wallpaper, the wake keys and
 * Quick Switch's size and rotation (seen with a simulated display, HyperOS 3.0.319).
 *
 * The back screen is the display that isn't the main one, isn't private to an app, and has the
 * 17 Pro Max (976 x 596) or 17 Pro (904 x 572) back screen's size either way round; failing
 * that, the one found last time, if it's still there. Plain Kotlin, so it's unit tested.
 */
object RearDisplayPick {
    private val REAR_SIZES = setOf(976 to 596, 904 to 572)

    // Display.DEFAULT_DISPLAY.
    private const val MAIN_DISPLAY = 0

    /** The back screen's id among [candidates], or null if none is; [rememberedId] is the one found last time. */
    fun choose(candidates: List<DisplayCandidate>, rememberedId: Int?): Int? {
        val others = candidates.filter { it.id != MAIN_DISPLAY && !it.isPrivate }
        val sized = others.filter { (maxOf(it.width, it.height) to minOf(it.width, it.height)) in REAR_SIZES }
        return sized.firstOrNull { it.id == rememberedId }?.id
            ?: sized.minByOrNull { it.id }?.id
            ?: others.firstOrNull { it.id == rememberedId }?.id
    }
}
