package com.app.rectonote.soundbank

import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature

class MidiTest {
    val tempoTrack = MidiTrack()
    val noteTrack = MidiTrack()

    val ts = TimeSignature()
    val tempo = Tempo()
    val track = ArrayList<MidiTrack>()
    val midi: MidiFile

    init {
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)
        tempo.bpm = 60F
        tempoTrack.insertEvent(ts)
        tempoTrack.insertEvent(tempo)
        noteTrack.insertNote(0, 60, 127, 0, 96)
        noteTrack.insertNote(0, 62, 127, 96 * 4, 96)
        noteTrack.insertNote(0, 64, 127, 96 * 8, 96)
        track.add(tempoTrack)
        track.add(noteTrack)
        midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, track)
    }


}