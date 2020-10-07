package com.app.rectonote.musictheory

class NoteUnit {
    var pitch: NotePitch = NotePitch.REST
    var octave = -1

    constructor(pitch: NotePitch, octave: Int) {
        this.pitch = pitch
        this.octave = octave
    }

    constructor(offset: Int, refNote: NoteUnit) {
        this.pitch = refNote.pitch
        this.octave = refNote.octave
        this.plus(offset)
    }

    //unit test this
    operator fun plus(offset: Int): NoteUnit {
        val octaveChange = this.octave + (offset / 12)
        val pitchShift: Int = (this.pitch.pitchNum + (offset % 12)) % 12
        val pitchChange = NotePitch.intToNotePitch(pitchShift)
        return NoteUnit(pitchChange, octaveChange)
    }

    operator fun minus(b: NoteUnit): Int {
        val octaveDiff = (b.octave - this.octave) * 12
        val pitchDiff = b.pitch.pitchNum - this.pitch.pitchNum
        return octaveDiff + pitchDiff
    }

    override operator fun equals(other: Any?): Boolean = if (other !is NoteUnit) false
    else (this.pitch == other.pitch) && (this.octave == other.octave)


    fun makeRestNote() {
        this.pitch = NotePitch.REST
        this.octave = -1
    }

    fun transpose(octave: Int) {
        this.plus(octave * 12)
    }

    override fun toString(): String {
        return "${this.pitch.pitchName}$octave"
    }

    companion object {
        fun transformNotes(offsetArray: Array<Int>, refNote: NoteUnit): Array<NoteUnit> =
            Array<NoteUnit>(offsetArray.size) { i ->
                if (offsetArray[i] == -1)
                    NoteUnit(NotePitch.REST, -1)
                else
                    NoteUnit(offsetArray[i], refNote)
            }
    }
}