package com.app.rectonote

import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import kotlin.test.Test
import kotlin.test.assertEquals

class NoteMinusTest {
    @Test
    @Throws(Exception::class)
    fun testNoteEquals() {
        val a = Note(NotePitch.C, 3)
        val b = Note(NotePitch.C, 3)
        assertEquals(0, a - b)
    }

    @Test
    @Throws(Exception::class)
    fun testNoteAMoreThanBInPitch() {
        val a = Note(NotePitch.Ab, 3)
        val b = Note(NotePitch.C, 3)
        assertEquals(8, a.minus(b))
    }

    @Test
    @Throws(Exception::class)
    fun testNoteALessThanBInPitch() {
        val a = Note(NotePitch.C, 3)
        val b = Note(NotePitch.Ab, 3)
        assertEquals(-8, a - b)
    }

    @Test
    @Throws(Exception::class)
    fun testNoteAMoreThanBInOctave() {
        val a = Note(NotePitch.Ab, 4)
        val b = Note(NotePitch.Ab, 3)
        assertEquals(12, a - b)
    }

    @Test
    @Throws(Exception::class)
    fun testNoteALessThanBInOctave() {
        val a = Note(NotePitch.Ab, 3)
        val b = Note(NotePitch.Ab, 4)
        assertEquals(-12, a - b)
    }

    @Test
    @Throws(Exception::class)
    fun testNoteAMoreThanBInCrossOctave() {
        val a = Note(NotePitch.C, 5)
        val b = Note(NotePitch.D, 4)
        assertEquals(10, a - b)
    }

    @Test
    @Throws(Exception::class)
    fun testNoteALessThanBInCrossOctave() {
        val a = Note(NotePitch.A, 3)
        val b = Note(NotePitch.Ab, 4)
        assertEquals(-11, a - b)
    }

    @Test
    @Throws(Exception::class)
    fun testNoteAMoreThanBInOverOctave() {
        val a = Note(NotePitch.C, 6)
        val b = Note(NotePitch.F, 4)
        assertEquals(19, a - b)
    }

    @Test
    @Throws(Exception::class)
    fun testNoteALessThanBInOverOctave() {
        val a = Note(NotePitch.D, 3)
        val b = Note(NotePitch.Eb, 6)
        assertEquals(-37, a - b)
    }
}