package com.app.rectonote

import com.app.rectonote.musictheory.Chord
import com.app.rectonote.musictheory.Key
import com.app.rectonote.musictheory.NotePitch.*
import com.app.rectonote.musictheory.PitchOperator
import com.app.rectonote.musictheory.TrackSequencer
import org.mockito.InjectMocks
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertTrue


class ChordCorrectTest {

    val pitchOperatorMock = mock(PitchOperator::class.java)


    @InjectMocks
    val trackSequencer = TrackSequencer(pitchOperator = pitchOperatorMock)

    @Test
    @Throws(Exception::class)
    fun `C Major Test = C Dm Em F G Am B`() {
        `when`(pitchOperatorMock.intToNotePitch(Key.C.ordinal)).thenReturn(C)
        `when`(pitchOperatorMock.plusPitch(C, 2)).thenReturn(D)
        `when`(pitchOperatorMock.plusPitch(C, 4)).thenReturn(E)
        `when`(pitchOperatorMock.plusPitch(C, 9)).thenReturn(A)
        val key = Key.C
        val input = arrayListOf(
            Chord(C, 3),
            Chord(D, 3),
            Chord(E, 3),
            Chord(F, 3),
            Chord(G, 3),
            Chord(A, 3),
            Chord(B, 3),
        )
        val expected = arrayListOf(
            Chord(C, 3).apply {
                chordType = "major"
            },
            Chord(D, 3).apply {
                chordType = "minor"
            },
            Chord(E, 3).apply {
                chordType = "minor"
            },
            Chord(F, 3).apply {
                chordType = "major"
            },
            Chord(G, 3).apply {
                chordType = "major"
            },
            Chord(A, 3).apply {
                chordType = "minor"
            },
            Chord(B, 3).apply {
                chordType = "major"
            },
        )
        val result = trackSequencer.chordCorrect(input, key).toArray()
        assertTrue {
            expected.toArray().contentEquals(result)
        }
    }

    @Test
    @Throws(Exception::class)
    fun `C Minor Test = Cm D Eb Fm Gm A B`() {
        `when`(pitchOperatorMock.intToNotePitch(Key.Cm.ordinal % 12)).thenReturn(C)
        `when`(pitchOperatorMock.plusPitch(C, 5)).thenReturn(F)
        `when`(pitchOperatorMock.plusPitch(C, 7)).thenReturn(G)
        val key = Key.Cm
        val input = arrayListOf(
            Chord(C, 3),
            Chord(D, 3),
            Chord(Eb, 3),
            Chord(F, 3),
            Chord(G, 3),
            Chord(A, 3),
            Chord(B, 3),
        )
        val expected = arrayListOf(
            Chord(C, 3).apply {
                chordType = "minor"
            },
            Chord(D, 3).apply {
                chordType = "major"
            },
            Chord(Eb, 3).apply {
                chordType = "major"
            },
            Chord(F, 3).apply {
                chordType = "minor"
            },
            Chord(G, 3).apply {
                chordType = "minor"
            },
            Chord(A, 3).apply {
                chordType = "major"
            },
            Chord(B, 3).apply {
                chordType = "major"
            },
        )
        val result = trackSequencer.chordCorrect(input, key).toArray()
        assertTrue {
            expected.toArray().contentEquals(result)
        }
    }

}