package com.app.rectonote

import com.app.rectonote.musictheory.Equal
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch.*
import com.app.rectonote.musictheory.TrackSequencer
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class RemoveNoiseTest {
    @Spy
    val equalsSpy = Equal()

    @InjectMocks
    val trackSequencerMock = TrackSequencer(equalsSpy)

    @Test
    @Throws(Exception::class)
    fun testBlankArray() {
        val input = arrayListOf(
            Note(C, 3).apply {
                lengthInFrame = 5
            }
        )
        val expected = arrayListOf(
            Note(C, 3).apply {
                lengthInFrame = 5
            }
        )
        val result = trackSequencerMock.removeNoise(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `input ' - N - ' become ' - - - '`() {
        val input = arrayListOf(
            Note(REST, -1).apply {
                lengthInFrame = 5
            },
            Note(E, 3).apply {
                lengthInFrame = 1
            },
            Note(REST, -1).apply {
                lengthInFrame = 5
            },
        )
        val expected = arrayListOf(
            Note(REST, -1).apply {
                lengthInFrame = 11
            },
        )
        val result = trackSequencerMock.removeNoise(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `input ' - N C ' become ' - (N+C) '`() {
        val input = arrayListOf(
            Note(REST, -1).apply {
                lengthInFrame = 5
            },
            Note(E, 3).apply {
                lengthInFrame = 1
            },
            Note(D, 3).apply {
                lengthInFrame = 5
            },
        )
        val expected = arrayListOf(
            Note(REST, -1).apply {
                lengthInFrame = 5
            },
            Note(D, 3).apply {
                lengthInFrame = 6
            },
        )
        val result = trackSequencerMock.removeNoise(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `input ' C N C ' become ' (C+N+C) '`() {
        val input = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 5
            },
            Note(E, 3).apply {
                lengthInFrame = 2
            },
            Note(C, 4).apply {
                lengthInFrame = 6
            },
        )
        val expected = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 13
            }
        )
        val result = trackSequencerMock.removeNoise(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `input ' C N D ' become ' (C+N) D '`() {
        val input = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 5
            },
            Note(E, 3).apply {
                lengthInFrame = 2
            },
            Note(D, 4).apply {
                lengthInFrame = 6
            },
        )
        val expected = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 7
            },
            Note(D, 4).apply {
                lengthInFrame = 6
            }
        )
        val result = trackSequencerMock.removeNoise(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `input ' C N - ' become ' (C+N) - '`() {
        val input = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 5
            },
            Note(E, 3).apply {
                lengthInFrame = 1
            },
            Note(REST, -1).apply {
                lengthInFrame = 6
            },
        )
        val expected = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 6
            },
            Note(REST, -1).apply {
                lengthInFrame = 6
            }
        )
        val result = trackSequencerMock.removeNoise(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `input ' C N C N N ' become ' C '`() {
        val input = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 5
            },
            Note(Gb, 7).apply {
                lengthInFrame = 2
            },
            Note(C, 4).apply {
                lengthInFrame = 5
            },
            Note(C, 3).apply {
                lengthInFrame = 2
            },
            Note(Db, 3).apply {
                lengthInFrame = 2
            },
        )
        val expected = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 16
            },
        )
        val result = trackSequencerMock.removeNoise(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `input ' N C ' become ' C '`() {
        val input = arrayListOf(
            Note(Gb, 7).apply {
                lengthInFrame = 2
            },
            Note(C, 4).apply {
                lengthInFrame = 5
            },
        )
        val expected = arrayListOf(
            Note(C, 4).apply {
                lengthInFrame = 7
            },
        )
        val result = trackSequencerMock.removeNoise(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }

    @Test
    @Throws(Exception::class)
    fun `input ' C N ' become ' C '`() {
        val input = arrayListOf(
            Note(Gb, 7).apply {
                lengthInFrame = 5
            },
            Note(C, 4).apply {
                lengthInFrame = 2
            },
        )
        val expected = arrayListOf(
            Note(Gb, 7).apply {
                lengthInFrame = 7
            },
        )
        val result = trackSequencerMock.removeNoise(input)
        assertTrue {
            expected.toArray().contentEquals(result.toArray())
        }
    }
}