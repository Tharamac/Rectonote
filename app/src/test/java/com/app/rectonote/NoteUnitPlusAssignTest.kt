package com.app.rectonote

import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import org.junit.Test
import kotlin.test.assertTrue

class NoteUnitPlusAssignTest {
    @Test
    @Throws(Exception::class)
    fun testPlusPitch() {
        val result = Note(NotePitch.C, 3)
        result += 5
        assertTrue {
            result.pitch == NotePitch.F && result.octave == 3
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPlusOctave() {
        val result = Note(NotePitch.C, 3)
        result += 12
        assertTrue {
            result.pitch == NotePitch.C && result.octave == 4
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPlusOverOctave() {
        val result = Note(NotePitch.C, 3)
        result += 19
        assertTrue {
            result.pitch == NotePitch.G && result.octave == 4
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPlusOverTwoOctave() {
        val result = Note(NotePitch.C, 3)
        result += 25
        assertTrue {
            result.pitch == NotePitch.Db && result.octave == 5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPlusZero() {
        val result = Note(NotePitch.C, 3)
        result += 0
        assertTrue {
            result.pitch == NotePitch.C && result.octave == 3
        }
    }

    @Test
    @Throws(Exception::class)
    fun testShiftDown() {
        val result = Note(NotePitch.G, 4)
        result += -9

        assertTrue {
            result.pitch == NotePitch.Bb && result.octave == 3
        }
    }

    @Test
    @Throws(Exception::class)
    fun testShiftOctaveDown() {
        val result = Note(NotePitch.G, 4)
        result += -12
        assertTrue {
            result.pitch == NotePitch.G && result.octave == 3
        }
    }

    @Test
    @Throws(Exception::class)
    fun testShiftOverOctaveDown() {
        val result = Note(NotePitch.Gb, 5)
        result += -16
        assertTrue {
            result.pitch == NotePitch.D && result.octave == 4
        }
    }

    @Test
    @Throws(Exception::class)
    fun testShiftOverTwoOctaveDown() {
        val result = Note(NotePitch.Gb, 5)
        result += -28
        assertTrue {
            result.pitch == NotePitch.D && result.octave == 3
        }
    }
}
