package com.app.rectonote

import com.app.rectonote.musictheory.Chord
import com.app.rectonote.musictheory.Equal
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch.*
import com.app.rectonote.musictheory.TrackSequencer
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class MelodyGenerateTrackTest {
    @Mock
    val equalsMock = Equal()

    @Spy
    @InjectMocks
    val trackSequencerMock = TrackSequencer(equalsMock)

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
        val actual = trackSequencerMock.generateTrack(rawNotes, "melody").toArray()

        assertTrue {
            expected.toArray()
                .contentEquals(actual)
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
        //   `when`(equalsMock.isSame(noteCapt.capture(), noteCapt.capture())).thenReturn(noteCapt.allValues[0].pitch == noteCapt.allValues[1].pitch && noteCapt.allValues[0].duration == noteCapt.allValues[1].duration)
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
        //`when`(equalsMock.isSame(chordCapt.capture(), chordCapt.capture())).thenReturn(chordCapt.allValues[0].pitch == chordCapt.allValues[1].pitch && chordCapt.allValues[0].duration == chordCapt.allValues[1].duration && chordCapt.allValues[0].chordType == chordCapt.allValues[1].chordType)

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
        //`when`(equalsMock.isSame(chordCapt.capture(), chordCapt.capture())).thenReturn(chordCapt.allValues[0].pitch == chordCapt.allValues[1].pitch && chordCapt.allValues[0].duration == chordCapt.allValues[1].duration && chordCapt.allValues[0].chordType == chordCapt.allValues[1].chordType)

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
        //`when`(equalsMock.isSame(noteCapt.capture(), noteCapt.capture())).thenReturn(noteCapt.allValues[0].pitch == noteCapt.allValues[1].pitch && noteCapt.allValues[0].duration == noteCapt.allValues[1].duration)
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
        //`when`(equalsMock.isSame(noteCapt.capture(), noteCapt.capture())).thenReturn(noteCapt.allValues[0].pitch == noteCapt.allValues[1].pitch && noteCapt.allValues[0].duration == noteCapt.allValues[1].duration)
        assertTrue {
            expected.toArray()
                .contentEquals(trackSequencerMock.generateTrack(rawNotes, "melody").toArray())
        }
    }
}