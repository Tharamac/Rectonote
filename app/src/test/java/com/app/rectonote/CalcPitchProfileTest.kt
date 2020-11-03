package com.app.rectonote

import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import com.app.rectonote.musictheory.TrackSequencer
import kotlin.test.Test

import kotlin.test.assertTrue

class CalcPitchProfileTest {

    @Test
    @Throws(Exception::class)
    fun testSomeZero() {
        val melody = arrayListOf<Note>(
            Note(NotePitch.A, 3).apply {
                lengthInFrame = 4
            },
            Note(NotePitch.C, 4).apply {
                lengthInFrame = 4
            },
            Note(NotePitch.E, 4).apply {
                lengthInFrame = 8
            }
        )
        //                               C  C# D  D# E  F  F# G  G# A  A# B
        val pitchExpected = arrayOf<Int>(4, 0, 0, 0, 8, 0, 0, 0, 0, 4, 0, 0)
        assertTrue {
            pitchExpected.contentEquals(TrackSequencer().calcPitchProfile(melody))
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNonZero() {
        val melody = arrayListOf<Note>(
            Note(NotePitch.C, 4).apply {
                lengthInFrame = 4
            },
            Note(NotePitch.Db, 4).apply {
                lengthInFrame = 4
            },
            Note(NotePitch.D, 4).apply {
                lengthInFrame = 8
            },
            Note(NotePitch.Eb, 4).apply {
                lengthInFrame = 15
            },
            Note(NotePitch.E, 4).apply {
                lengthInFrame = 4
            },
            Note(NotePitch.F, 4).apply {
                lengthInFrame = 8
            },
            Note(NotePitch.Gb, 4).apply {
                lengthInFrame = 4
            },
            Note(NotePitch.G, 4).apply {
                lengthInFrame = 4
            },
            Note(NotePitch.Ab, 4).apply {
                lengthInFrame = 8
            },
            Note(NotePitch.A, 4).apply {
                lengthInFrame = 8
            },
            Note(NotePitch.Bb, 4).apply {
                lengthInFrame = 8
            },
            Note(NotePitch.B, 4).apply {
                lengthInFrame = 8
            }
        )
        //                               C  C# D  D# E  F  F# G  G# A  A# B
        val pitchExpected = arrayOf<Int>(4, 4, 8, 15, 4, 8, 4, 4, 8, 8, 8, 8)
        assertTrue {
            pitchExpected.contentEquals(TrackSequencer().calcPitchProfile(melody))
        }
    }

    @Test
    @Throws(Exception::class)
    fun testRepeatedNote() {
        val melody = arrayListOf<Note>(
            Note(NotePitch.C, 3).apply {
                lengthInFrame = 4
            },
            Note(NotePitch.C, 4).apply {
                lengthInFrame = 4
            },
            Note(NotePitch.C, 5).apply {
                lengthInFrame = 8
            }
        )
        //                               C  C# D  D# E  F  F# G  G# A  A# B
        val pitchExpected = arrayOf<Int>(16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        assertTrue {
            pitchExpected.contentEquals(TrackSequencer().calcPitchProfile(melody))
        }
    }
}