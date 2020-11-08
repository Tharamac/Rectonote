package com.app.rectonote.musictheory

class Chord(pitch: NotePitch, octave: Int) : Note(pitch, octave) {
    var chordType: String = "major"
    override fun equals(other: Any?): Boolean =
        if (other !is Chord) false else (super.equals(other)) && (chordType == other.chordType)

    override fun toString(): String =
        "${super.toString()} ${if (chordType.toLowerCase() == "minor") "m" else ""}\tframelen: $lengthInFrame\tduration: $duration"
}