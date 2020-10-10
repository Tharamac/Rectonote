package com.app.rectonote

import com.app.rectonote.musictheory.NotePitch
import com.app.rectonote.musictheory.NoteUnit
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoteUnitEqualTest {
    @Test
    @Throws(Exception::class)
    fun testNoteUnitSameBoth() {
        val a = NoteUnit(NotePitch.C, 3)
        val b = NoteUnit(NotePitch.C, 3)
        assertTrue {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteUnitDiffPitch() {
        val a = NoteUnit(NotePitch.C, 3)
        val b = NoteUnit(NotePitch.D, 3)
        assertFalse {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteUnitDiffOctave() {
        val a = NoteUnit(NotePitch.C, 4)
        val b = NoteUnit(NotePitch.C, 3)
        assertFalse {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteUnitDiffBoth() {
        val a = NoteUnit(NotePitch.D, 4)
        val b = NoteUnit(NotePitch.C, 3)
        assertFalse {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteSameBoth() {
        val a = Note(NotePitch.C, 3)
        val b = Note(NotePitch.C, 3)
        assertTrue {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffPitch() {
        val a = NoteUnit(NotePitch.C, 3)
        val b = NoteUnit(NotePitch.Gb, 3)
        assertFalse {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffOctave() {
        val a = NoteUnit(NotePitch.C, 5)
        val b = NoteUnit(NotePitch.C, 3)
        assertFalse {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffBoth() {
        val a = Note(NotePitch.D, 4)
        val b = Note(NotePitch.C, 3)
        assertFalse {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffLengthInFrame() {
        val a = Note(NotePitch.C, 4)
        a.lengthInFrame = 5
        val b = Note(NotePitch.C, 4)
        a.lengthInFrame = 3
        assertTrue {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffDuration() {
        val a = Note(NotePitch.C, 4)
        a.duration = 5
        val b = Note(NotePitch.C, 4)
        a.duration = -1
        assertTrue {
            a == b
        }
    }
}