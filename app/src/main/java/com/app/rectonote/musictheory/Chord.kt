package com.app.rectonote.musictheory

class Chord(pitch: NotePitch, octave: Int, var chordType: String) : NoteUnit(pitch, octave) {
    var lengthInFrame = 1
    var duration = -1
    var lengthInSecond = -1.00
    override fun toString(): String =
        "${super.toString()} ${if (chordType.toLowerCase() == "minor") "m" else ""}\tframelen: $lengthInFrame\tduration: $duration"
}