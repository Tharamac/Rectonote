package com.app.rectonote.musictheory

open class Note(var pitch: NotePitch, var octave: Int) {
    var lengthInFrame = 1
    var duration = -1
    var lengthInSecond = -1.00

    constructor(offset: Int, refNote: Note) : this(refNote.pitch, refNote.octave) {
        this.plusAssign(offset)
    }

    //unit test this
    operator fun plusAssign(offset: Int) {
        val octaveChange = this.octave + (offset / 12)
        val pitchShift: Int = (this.pitch.pitchNum + (offset % 12)) % 12
        val pitchChange = NotePitch.intToNotePitch(pitchShift)
        this.octave = octaveChange
        this.pitch = pitchChange
    }

    operator fun minus(b: Note): Int {
        val octaveDiff = (b.octave - this.octave) * 12
        val pitchDiff = b.pitch.pitchNum - this.pitch.pitchNum
        return octaveDiff + pitchDiff
    }

    override operator fun equals(other: Any?): Boolean = if (other !is Note) false
    else (this.pitch == other.pitch) && (this.octave == other.octave)


    fun makeRestNote() {
        this.pitch = NotePitch.REST
        this.octave = -1
    }

    fun transpose(octave: Int) {
        this.plusAssign(octave * 12)
    }

    override fun toString(): String {
        return "${this.pitch.pitchName}${if (this.octave != -1) octave else ""}"
    }

    companion object {
        fun transformNotes(offsetArray: IntArray, refNote: Note): Array<Note> =
            Array<Note>(offsetArray.size) { i ->
                if (offsetArray[i] == -1)
                    Note(NotePitch.REST, -1)
                else
                    Note(offsetArray[i], refNote)
            }
    }
}