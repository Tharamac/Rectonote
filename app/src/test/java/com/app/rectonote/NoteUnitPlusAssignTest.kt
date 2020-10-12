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
}