package com.app.rectonote

import com.app.rectonote.musictheory.Chord
import com.app.rectonote.musictheory.Equal
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoteEqualTest {
    val comparator = Equal()

    @Test
    @Throws(Exception::class)
    fun testNoteSameBoth() {
        val a = Note(NotePitch.C, 3)
        val b = Note(NotePitch.C, 3)
        assertTrue {
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffBoth() {
        val a = Note(NotePitch.D, 4)
        val b = Note(NotePitch.C, 3)
        assertFalse {
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffPitch() {
        val a = Note(NotePitch.C, 3)
        val b = Note(NotePitch.Gb, 3)
        assertFalse {
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffOctave() {
        val a = Note(NotePitch.C, 5)
        val b = Note(NotePitch.C, 3)
        assertFalse {
            comparator.isSame(a, b)
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
            comparator.isSame(a, b)
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
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testRestNote() {
        val a = Note(NotePitch.REST, -1)
        val b = Note(NotePitch.REST, -1)
        assertTrue {
            comparator.isSame(a, b)
        }
    }


    //Chord

    @Test
    @Throws(Exception::class)
    fun testChordSameBoth() {
        val a = Chord(NotePitch.C, 3)
        val b = Chord(NotePitch.C, 3)
        assertTrue {
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testChordDiffBoth() {
        val a = Chord(NotePitch.D, 4)
        val b = Chord(NotePitch.C, 3)
        assertFalse {
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testChordDiffPitch() {
        val a = Chord(NotePitch.C, 3)
        val b = Chord(NotePitch.Gb, 3)
        assertFalse {
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testChordDiffOctave() {
        val a = Chord(NotePitch.C, 5)
        val b = Chord(NotePitch.C, 3)
        assertFalse {
            comparator.isSame(a, b)
        }
    }


    @Test
    @Throws(Exception::class)
    fun testChordDiffLengthInFrame() {
        val a = Note(NotePitch.C, 4)
        a.lengthInFrame = 5
        val b = Note(NotePitch.C, 4)
        a.lengthInFrame = 3
        assertTrue {
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testChordDiffDuration() {
        val a = Chord(NotePitch.C, 4)
        a.duration = 5
        val b = Chord(NotePitch.C, 4)
        a.duration = -1
        assertTrue {
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testRestChord() {
        val a = Chord(NotePitch.REST, -1)
        val b = Chord(NotePitch.REST, -1)
        assertTrue {
            comparator.isSame(a, b)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testChordDiffType() {
        val a = Chord(NotePitch.F, 3).apply { chordType = "major" }
        val b = Chord(NotePitch.F, 3).apply { chordType = "minor" }
        assertFalse {
            comparator.isSame(a, b)
        }
    }


}