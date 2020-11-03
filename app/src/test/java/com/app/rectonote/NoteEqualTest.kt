package com.app.rectonote

import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoteEqualTest {

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
    fun testNoteDiffBoth() {
        val a = Note(NotePitch.D, 4)
        val b = Note(NotePitch.C, 3)
        assertFalse {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffPitch() {
        val a = Note(NotePitch.C, 3)
        val b = Note(NotePitch.Gb, 3)
        assertFalse {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteDiffOctave() {
        val a = Note(NotePitch.C, 5)
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

    @Test
    @Throws(Exception::class)
    fun testRestNote() {
        val a = Note(NotePitch.REST, -1)
        val b = Note(NotePitch.REST, -1)
        assertTrue {
            a == b
        }
    }

    @Test
    @Throws(Exception::class)
    fun testRestNoteWithMakeRestNote() {
        val a = Note(NotePitch.A, 3)
        val expected = Note(NotePitch.REST, -1)
        a.makeRestNote()
        assertTrue {
            a == expected
        }
    }
}