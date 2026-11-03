package com.backscreen.wallpaper.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RearDisplayPickTest {

    // The 17 Pro Max's own two, as `dumpsys display` lists them (HyperOS 3.0.319).
    private val main = DisplayCandidate(0, 1200, 2608)
    private val rear = DisplayCandidate(1, 976, 596)

    // What Android lists ahead of them: a cast screen, or a simulated display (seen as "Overlay #1").
    private val cast = DisplayCandidate(2, 1920, 1080)
    private val overlay = DisplayCandidate(3, 1920, 1080)

    @Test
    fun theBackScreenOverACastOrSimulatedDisplay() {
        assertEquals(1, RearDisplayPick.choose(listOf(cast, overlay, main, rear), rememberedId = null))
        assertEquals(1, RearDisplayPick.choose(listOf(main, rear), rememberedId = 1))
    }

    @Test
    fun turnedToPortraitByAnAppItsStillTheBackScreen() {
        assertEquals(1, RearDisplayPick.choose(listOf(main, cast, DisplayCandidate(1, 596, 976)), rememberedId = null))
    }

    @Test
    fun the17ProBackScreenInEitherOrientationOverAnExternalDisplay() {
        val proMain = DisplayCandidate(0, 1220, 2656)
        for ((width, height) in listOf(904 to 572, 572 to 904)) {
            val proRear = DisplayCandidate(1, width, height)
            assertEquals(1, RearDisplayPick.choose(listOf(cast, overlay, proMain, proRear), rememberedId = null))
            assertEquals(1, RearDisplayPick.choose(listOf(cast, proMain, proRear), rememberedId = cast.id))
        }
    }

    @Test
    fun theTwoModelsDimensionsCannotBeMixed() {
        val mixed = listOf(DisplayCandidate(1, 976, 572), DisplayCandidate(2, 904, 596))
        assertNull(RearDisplayPick.choose(listOf(main) + mixed, rememberedId = null))
    }

    @Test
    fun anAppsPrivateDisplayIsNeverIt() {
        for ((width, height) in listOf(976 to 596, 904 to 572)) {
            val private = DisplayCandidate(7, width, height, isPrivate = true)
            assertNull(RearDisplayPick.choose(listOf(main, private), rememberedId = null))
            assertEquals(1, RearDisplayPick.choose(listOf(main, private, rear), rememberedId = null))
        }
    }

    @Test
    fun theOneFoundLastTimeIfNoneHasItsSize() {
        val resized = DisplayCandidate(1, 1080, 660)
        assertEquals(1, RearDisplayPick.choose(listOf(main, cast, resized), rememberedId = 1))
        // Not if it has gone.
        assertNull(RearDisplayPick.choose(listOf(main, cast), rememberedId = 1))
    }

    @Test
    fun noneWithOnlyAnExternalDisplay() {
        assertNull(RearDisplayPick.choose(listOf(main, cast), rememberedId = null))
        assertNull(RearDisplayPick.choose(listOf(main), rememberedId = null))
    }

    @Test
    fun theMainScreenIsNeverIt() {
        for ((width, height) in listOf(976 to 596, 904 to 572)) {
            assertNull(RearDisplayPick.choose(listOf(DisplayCandidate(0, width, height)), rememberedId = 0))
        }
    }
}
