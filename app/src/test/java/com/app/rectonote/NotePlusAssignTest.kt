package com.app.rectonote

import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import com.app.rectonote.musictheory.PitchOperator
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)

class NotePlusAssignTest {
    val pitchMock = mock(PitchOperator::class.java)

    @Test
    @Throws(Exception::class)
    fun testPlusPitch() {
        val result = Note(NotePitch.C, 3)
        `when`(pitchMock.intToNotePitch(anyInt())).thenReturn(NotePitch.F)
        result += 5
        assertTrue {
            result.pitch == NotePitch.F && result.octave == 3
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPlusOctave() {
        val result = Note(NotePitch.C, 3)
        `when`(pitchMock.intToNotePitch(anyInt())).thenReturn(NotePitch.C)
        result += 12
        assertTrue {
            result.pitch == NotePitch.C && result.octave == 4
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPlusOverOctave() {
        val result = Note(NotePitch.C, 3)
        `when`(pitchMock.intToNotePitch(anyInt())).thenReturn(NotePitch.G)
        result += 19
        assertTrue {
            result.pitch == NotePitch.G && result.octave == 4
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPlusOverTwoOctave() {
        val result = Note(NotePitch.C, 3)
        `when`(pitchMock.intToNotePitch(anyInt())).thenReturn(NotePitch.Db)
        result += 25
        assertTrue {
            result.pitch == NotePitch.Db && result.octave == 5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPlusZero() {
        val result = Note(NotePitch.C, 3)
        `when`(pitchMock.intToNotePitch(anyInt())).thenReturn(NotePitch.C)
        result += 0
        assertTrue {
            result.pitch == NotePitch.C && result.octave == 3
        }
    }

    @Test
    @Throws(Exception::class)
    fun testShiftDown() {
        val result = Note(NotePitch.G, 4)
        `when`(pitchMock.intToNotePitch(anyInt())).thenReturn(NotePitch.Bb)
        result += -9

        assertTrue {
            result.pitch == NotePitch.Bb && result.octave == 3
        }
    }

    @Test
    @Throws(Exception::class)
    fun testShiftOctaveDown() {
        val result = Note(NotePitch.G, 4)
        `when`(pitchMock.intToNotePitch(anyInt())).thenReturn(NotePitch.G)
        result += -12

        assertTrue {
            result.pitch == NotePitch.G && result.octave == 3
        }
    }

    @Test
    @Throws(Exception::class)
    fun testShiftOverOctaveDown() {
        val result = Note(NotePitch.Gb, 5)
        `when`(pitchMock.intToNotePitch(anyInt())).thenReturn(NotePitch.D)
        result += -16

        assertTrue {
            result.pitch == NotePitch.D && result.octave == 4
        }
    }

    @Test
    @Throws(Exception::class)
    fun testShiftOverTwoOctaveDown() {
        val result = Note(NotePitch.Gb, 5)
        `when`(pitchMock.intToNotePitch(anyInt())).thenReturn(NotePitch.D)
        result += -28

        assertTrue {
            result.pitch == NotePitch.D && result.octave == 3
        }
    }
}
