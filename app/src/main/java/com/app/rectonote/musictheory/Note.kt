package com.app.rectonote.musictheory

class Note(pitch: NotePitch, octave: Int) : NoteUnit(pitch, octave) {
    var lengthInFrame = 1
    var duration = -1
    var lengthInSecond = -1.00
    override fun toString(): String =
        "${super.toString()}\tframelen: $lengthInFrame\tduration: $duration"
}
