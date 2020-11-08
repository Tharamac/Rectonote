package com.app.rectonote.musictheory

open class Equal {
    open fun isSame(a: Note, b: Note): Boolean = a.pitch == b.pitch && a.octave == b.octave
    open fun isSame(a: Chord, b: Chord): Boolean =
        a.pitch == b.pitch && a.octave == b.octave && a.chordType == b.chordType
}