package com.app.rectonote

import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import org.junit.Test
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
        val input = IntArray(100) { it }
        val expected = Array<Note>(100) {
            val temp = Note(NotePitch.C, 3)
            temp.plusAssign(it)
            temp
        }
        val result = Note.transformNotes(input, refNote)
        assertTrue {
            expected.contentEquals(result)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNoteNegative() {
        val input = IntArray(100) { -it }
        val expected = Array<Note>(100) {
            val temp = Note(NotePitch.C, 3)
            temp.plusAssign(-it)
            temp
        }
        val result = Note.transformNotes(input, refNote)
        assertTrue {
            expected.contentEquals(result)
        }
    }
}