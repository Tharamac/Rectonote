package com.app.rectonote.midiplayback

import com.app.rectonote.musictheory.Chord
import com.app.rectonote.musictheory.DraftTrackData
import com.app.rectonote.musictheory.NotePitch
import com.app.rectonote.musictheory.NotePitch.*
import java.util.*

class MIDIPlayerChannel(draftTrack: DraftTrackData) {
    val NANOS_PER_SECOND = 1000000000L
    val key = draftTrack.key
    val tempo = draftTrack.tempo
    val milliSecondsPerDuration = ((60.0 / tempo) / 4) * 1000
    val trackSequence = draftTrack.trackSequence
    var muted = false
    var isPlaying = false
    val duration = trackSequence.sumBy { it.duration }


    companion object {
        init {
            System.loadLibrary("midiplayer_interface")
        }
    }


    external fun nativeNew(): Long
    external fun nativeLoadSoundfont(soundfont_path: String)
    external fun nativeLoadPreset(channel: Int, bankNumber: Int, progNumber: Int)
    external fun nativeLoadMidiFile(midiPath: String)
    external fun nativePlayMidi()
    external fun nativeStopMidi()

    external fun nativePlaySingleNote(
        channel: Int,
        midiNoteNumber: Int,
        velocity: Int,
        durationInMs: Double
    )

    external fun nativePlayRestNote(durationInMs: Double)
    external fun nativeStopMessage(channel: Int)

    external fun nativeRemovePlayer()
    external fun nativePlayChord(
        channel: Int,
        rootNoteNumber: Int,
        chordType: Int,
        velocity: Int,
        durationInMs: Double
    )

    val nativeObjectPointer = nativeNew()

    fun playDraftTrackSequence() {
        isPlaying = true
        if (!muted) {
            trackSequence.forEach {
                if (!isPlaying) return@forEach
                val durationInMs = it.duration * milliSecondsPerDuration
                when (it) {
                    is Chord -> {
                        if (it.pitch == REST) {
                            nativePlayRestNote((durationInMs))
                        } else {
                            nativePlayChord(
                                0,
                                convertNoteIntoNoteNumber(it.pitch, it.octave),
                                if (it.chordType.toLowerCase(Locale.ROOT) == "major") 0 else 1,
                                127,
                                durationInMs
                            )
                        }
                    }
                    else -> {
                        if (it.pitch == REST)
                            nativePlayRestNote((durationInMs))
                        else
                            nativePlaySingleNote(
                                0,
                                convertNoteIntoNoteNumber(it.pitch, it.octave),
                                127,
                                durationInMs
                            )
                    }
                }
            }
        }
        isPlaying = false
    }

    fun stopMessage() {
        isPlaying = false
    }

    private fun convertNoteIntoNoteNumber(pitch: NotePitch, octave: Int): Int =
        pitch.pitchNum + ((octave + 1) * 12)

    private fun convertChordIntoNoteNumber(
        pitch: NotePitch,
        octave: Int,
        chordType: String
    ): ByteArray {
        val rootNoteNumber = pitch.pitchNum + ((octave + 1) * 12)
        val thirdNoteNumber = if (chordType == "major") {
            rootNoteNumber + 4
        } else rootNoteNumber + 3
        val fifthNoteNumber = rootNoteNumber + 7
        return byteArrayOf(
            rootNoteNumber.toByte(),
            thirdNoteNumber.toByte(),
            fifthNoteNumber.toByte()
        )
    }

    protected fun finalize() {
        nativeRemovePlayer()
    }


}