package com.app.rectonote

import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import kotlin.test.Test
import kotlin.test.assertTrue


class TransformNotesTest {
    private val refNote = Note(NotePitch.C, 3)

    @Test
    @Throws(Exception::class)
    fun testRestNote() {
        val input = intArrayOf(-999)
        val expected = arrayOf<Note>(Note(NotePitch.REST, -1))
        val result = Note.transformNotes(input, refNote)
        assertTrue {
            expected.contentEquals(result)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNotePositive() {
        val input = IntArray(49) { it }
        val expected = arrayOf<Note>(
            Note(NotePitch.C, 3),
            Note(NotePitch.Db, 3),
            Note(NotePitch.D, 3),
            Note(NotePitch.Eb, 3),
            Note(NotePitch.E, 3),
            Note(NotePitch.F, 3),
            Note(NotePitch.Gb, 3),
            Note(NotePitch.G, 3),
            Note(NotePitch.Ab, 3),
            Note(NotePitch.A, 3),
            Note(NotePitch.Bb, 3),
            Note(NotePitch.B, 3),
            Note(NotePitch.C, 4),
            Note(NotePitch.Db, 4),
            Note(NotePitch.D, 4),
            Note(NotePitch.Eb, 4),
            Note(NotePitch.E, 4),
            Note(NotePitch.F, 4),
            Note(NotePitch.Gb, 4),
            Note(NotePitch.G, 4),
            Note(NotePitch.Ab, 4),
            Note(NotePitch.A, 4),
            Note(NotePitch.Bb, 4),
            Note(NotePitch.B, 4),
            Note(NotePitch.C, 5),
            Note(NotePitch.Db, 5),
            Note(NotePitch.D, 5),
            Note(NotePitch.Eb, 5),
            Note(NotePitch.E, 5),
            Note(NotePitch.F, 5),
            Note(NotePitch.Gb, 5),
            Note(NotePitch.G, 5),
            Note(NotePitch.Ab, 5),
            Note(NotePitch.A, 5),
            Note(NotePitch.Bb, 5),
            Note(NotePitch.B, 5),
            Note(NotePitch.C, 6),
            Note(NotePitch.Db, 6),
            Note(NotePitch.D, 6),
            Note(NotePitch.Eb, 6),
            Note(NotePitch.E, 6),
            Note(NotePitch.F, 6),
            Note(NotePitch.Gb, 6),
            Note(NotePitch.G, 6),
            Note(NotePitch.Ab, 6),
            Note(NotePitch.A, 6),
            Note(NotePitch.Bb, 6),
            Note(NotePitch.B, 6),
            Note(NotePitch.C, 7),
        )
        val result = Note.transformNotes(input, refNote)
        assertTrue {
            expected.contentEquals(result)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteNegative() {
        val input = IntArray(12) { -it - 1 }
        val expected = arrayOf(
            Note(NotePitch.C, 2),
            Note(NotePitch.Db, 2),
            Note(NotePitch.D, 2),
            Note(NotePitch.Eb, 2),
            Note(NotePitch.E, 2),
            Note(NotePitch.F, 2),
            Note(NotePitch.Gb, 2),
            Note(NotePitch.G, 2),
            Note(NotePitch.Ab, 2),
            Note(NotePitch.A, 2),
            Note(NotePitch.Bb, 2),
            Note(NotePitch.B, 2),
        ).also {
            it.reverse()
        }
        val result = Note.transformNotes(input, refNote)
        assertTrue {
            expected.contentEquals(result)
        }
    }

}