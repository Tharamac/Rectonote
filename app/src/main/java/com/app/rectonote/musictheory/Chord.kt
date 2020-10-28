package com.app.rectonote.musictheory

class Chord(pitch: NotePitch, octave: Int) : Note(pitch, octave) {
    var chordType: String = "major"
    override fun toString(): String =
        "${super.toString()} ${if (chordType.toLowerCase() == "minor") "m" else ""}\tframelen: $lengthInFrame\tduration: $duration"
}