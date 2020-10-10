package com.app.rectonote

import com.app.rectonote.musictheory.NotePitch
import com.app.rectonote.musictheory.NoteUnit
import org.junit.Test

class NoteUnitPlusAssignTest {
    @Test
    @Throws(Exception::class)
    fun testNoteUnitSameBoth() {
        val note = NoteUnit(NotePitch.C, 3)
        note += 15
        val expected = NoteUnit
//        assertTrue{
//
//        }
    }
}