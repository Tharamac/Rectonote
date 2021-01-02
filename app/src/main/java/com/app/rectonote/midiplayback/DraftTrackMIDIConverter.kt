package com.app.rectonote.midiplayback

import com.app.rectonote.musictheory.Chord
import com.app.rectonote.musictheory.DraftTrackData
import com.app.rectonote.musictheory.NotePitch
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature

class DraftTrackMIDIConverter(draftTrackData: DraftTrackData) {
    val NANOS_PER_SECOND = 1000000000L
    val key = draftTrackData.key
    val tempo = draftTrackData.tempo
    val secondsPerDuration = (60.0 / tempo) / 4
    val trackSequence = draftTrackData.trackSequence

    fun convert(): ArrayList<MIDIEvent> {
        val events = ArrayList<MIDIEvent>()
        var currentTimeNanoSec: Long = 0
        if (trackSequence[0] !is Chord)
            trackSequence.forEach {
                val timeDuration = it.duration * secondsPerDuration
                val timeDurationNanoSec = (timeDuration * NANOS_PER_SECOND).toLong()
                if (it.pitch == NotePitch.REST && it.octave == -1)
                    currentTimeNanoSec += timeDurationNanoSec
                else {
                    val noteNumber: Byte = convertNoteIntoNoteNumber(it.pitch, it.octave).toByte()
                    val noteOn = MIDIEvent(true, 0, noteNumber, 127, currentTimeNanoSec)
                    currentTimeNanoSec += timeDurationNanoSec
                    val noteOff = MIDIEvent(false, 0, noteNumber, 0, currentTimeNanoSec)
                    events.add(noteOn)
                    events.add(noteOff)
                }

            }
        else
            trackSequence.forEach {
                val timeDuration = (it.duration * secondsPerDuration).toLong()
                val timeDurationNanoSec = timeDuration * NANOS_PER_SECOND
                if (it.pitch == NotePitch.REST && it.octave == -1)
                    currentTimeNanoSec += timeDurationNanoSec
                else {
                    val chordNoteNumber =
                        convertChordIntoNoteNumber(it.pitch, it.octave, (it as Chord).chordType)
                    val noteOn = arrayOf(
                        MIDIEvent(true, 0, chordNoteNumber[0], 127, currentTimeNanoSec),
                        MIDIEvent(true, 0, chordNoteNumber[1], 127, currentTimeNanoSec),
                        MIDIEvent(true, 0, chordNoteNumber[2], 127, currentTimeNanoSec),
                    )
                    currentTimeNanoSec += (timeDuration * NANOS_PER_SECOND)
                    val noteOff = arrayOf(
                        MIDIEvent(false, 0, chordNoteNumber[0], 0, currentTimeNanoSec),
                        MIDIEvent(false, 0, chordNoteNumber[1], 0, currentTimeNanoSec),
                        MIDIEvent(false, 0, chordNoteNumber[2], 0, currentTimeNanoSec),
                    )
                    events.addAll(noteOn)
                    events.addAll(noteOff)
                }

            }
        return events
    }

    fun convertToMidiData(): MidiFile {
        val tempoTrack = MidiTrack()
        val noteTrack = MidiTrack()
        val ts = TimeSignature()
        val tempo = Tempo()
        val track = ArrayList<MidiTrack>()
        //96 ticks per quarter note
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)
        tempo.bpm = this.tempo.toFloat()
        tempoTrack.insertEvent(ts)
        tempoTrack.insertEvent(tempo)
        if (trackSequence[0] !is Chord)
            trackSequence.forEach {

            }
        else
            trackSequence.forEach {}
        noteTrack.insertNote(0, 60, 127, 0, 24)
        noteTrack.insertNote(0, 62, 127, 48, 24)
        noteTrack.insertNote(0, 64, 127, 96, 24)
        track.add(tempoTrack)
        track.add(noteTrack)
        return MidiFile(MidiFile.DEFAULT_RESOLUTION, track)
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
}