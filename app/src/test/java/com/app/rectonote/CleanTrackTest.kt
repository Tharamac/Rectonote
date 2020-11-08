package com.app.rectonote

import com.app.rectonote.musictheory.Equal
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import com.app.rectonote.musictheory.TrackSequencer
import org.mockito.InjectMocks
import org.mockito.Spy
import kotlin.test.Test
import kotlin.test.assertTrue

class CleanTrackTest {
    @Spy
    val equalsSpy = Equal()


    @InjectMocks
    val trackSequencerMock = TrackSequencer(equalsSpy)

    @Test
    @Throws(Exception::class)
    fun trimRestBothFirstAndLast() {
        val input = arrayListOf(
            Note(NotePitch.REST, -1).apply {
                lengthInFrame = 5
            },
            Note(NotePitch.C, 3).apply {
                lengthInFrame = 5
            },
            Note(NotePitch.REST, -1).apply {
                lengthInFrame = 5
            },
        )
        val expected = arrayListOf(
            Note(NotePitch.C, 3).apply {
                lengthInFrame = 5
            }
        )
        val result = trackSequencerMock.cleanTrack(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun trimAdjacentDuplicate() {
        val input = arrayListOf(
            Note(NotePitch.C, 3).apply {
                lengthInFrame = 5
            },
            Note(NotePitch.C, 3).apply {
                lengthInFrame = 5
            },
        )
        val expected = arrayListOf(
            Note(NotePitch.C, 3).apply {
                lengthInFrame = 10
            }
        )
        val result = trackSequencerMock.cleanTrack(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }
}