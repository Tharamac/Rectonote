package com.app.rectonote

import com.app.rectonote.musictheory.Chord
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch.*
import com.app.rectonote.musictheory.TrackSequencer
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class MelodyGenerateTest {
    @Mock
    val trackSequencerMock = TrackSequencer()


    @Test
    @Throws(Exception::class)
    fun `Melody must contains C4(size = 3), D4(size = 7)`() {

        val rawNotes = arrayOf(
            Note(C, 4),
            Note(C, 4),
            Note(C, 4),
            Note(D, 4),
            Note(D, 4),
            Note(D, 4),
            Note(D, 4),
            Note(D, 4),
            Note(D, 4),
            Note(D, 4),
        )
        val expected = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 3
            },
            Note(D, 4).apply {
                lengthInFrame = 7
            }
        )
        `when`(trackSequencerMock.initTrack(rawNotes)).thenReturn(0)
        assertTrue {
            expected.toArray()
                .contentEquals(trackSequencerMock.generateTrack(rawNotes, "melody").toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `Melody must contains C3(size = 3) and ignore rest notes`() {

        val rawNotes = arrayOf(
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(C, 4),
            Note(C, 4),
            Note(C, 4),
        )
        val expected = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 3
            },
        )
        `when`(trackSequencerMock.initTrack(rawNotes)).thenReturn(5)
        assertTrue {
            expected.toArray()
                .contentEquals(trackSequencerMock.generateTrack(rawNotes, "melody").toArray())
        }
    }


    @Test
    @Throws(Exception::class)
    fun `Chord Progression must contains C4 Major(size = 3), D4 Major(size = 7)`() {

        val rawNotes = arrayOf(
            Note(C, 4),
            Note(C, 4),
            Note(C, 4),
            Note(D, 4),
            Note(D, 4),
            Note(D, 4),
            Note(F, 4),
            Note(E, 4),
            Note(E, 4),
            Note(E, 4),
            Note(E, 4),
        )
        val expected = arrayListOf(
            Chord(C, 4).apply {
                lengthInFrame = 3
            },
            Chord(D, 4).apply {
                lengthInFrame = 3
            },
            Chord(F, 4).apply {
                lengthInFrame = 1
            },
            Chord(E, 4).apply {
                lengthInFrame = 4
            }
        )
        `when`(trackSequencerMock.initTrack(rawNotes)).thenReturn(0)
        assertTrue {
            expected.toArray()
                .contentEquals(trackSequencerMock.generateTrack(rawNotes, "chord").toArray())
        }
    }


    @Test
    @Throws(Exception::class)
    fun `Chord Progression must contains C3 Major(size = 3) and ignore rest notes`() {

        val rawNotes = arrayOf(
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(C, 4),
            Note(C, 4),
            Note(C, 4),
            Note(C, 4),
            Note(C, 4),
            Note(C, 4),
        )
        val expected = arrayListOf(
            Chord(C, 4).apply {
                lengthInFrame = 6
            },
        )
        `when`(trackSequencerMock.initTrack(rawNotes)).thenReturn(6)
        assertTrue {
            expected.toArray()
                .contentEquals(trackSequencerMock.generateTrack(rawNotes, "chord").toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `Nothing`() {

        val rawNotes = emptyArray<Note>()
        val expected = ArrayList<Note>()
        `when`(trackSequencerMock.initTrack(rawNotes)).thenReturn(-1)
        assertTrue {
            expected.toArray()
                .contentEquals(trackSequencerMock.generateTrack(rawNotes, "melody").toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `Rest notes generate nothing`() {


        val rawNotes = arrayOf(
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
            Note(REST, -1),
        )
        val expected = ArrayList<Note>()
        `when`(trackSequencerMock.initTrack(rawNotes)).thenReturn(-1)
        assertTrue {
            expected.toArray()
                .contentEquals(trackSequencerMock.generateTrack(rawNotes, "melody").toArray())
        }
    }
}