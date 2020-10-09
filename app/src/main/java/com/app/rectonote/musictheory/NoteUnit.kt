package com.app.rectonote.musictheory

open class NoteUnit(var pitch: NotePitch, open var octave: Int) {
    constructor(offset: Int, refNote: NoteUnit) : this(refNote.pitch, refNote.octave) {
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
        this.plusAssign(octave * 12)
    }

    override fun toString(): String {
        return "${this.pitch.pitchName}${if (this.octave != -1) octave else ""}"
    }

    companion object {
        fun transformNotes(offsetArray: IntArray, refNote: NoteUnit): Array<NoteUnit> =
            Array<NoteUnit>(offsetArray.size) { i ->
                if (offsetArray[i] == -1)
                    NoteUnit(NotePitch.REST, -1)
                else
                    NoteUnit(offsetArray[i], refNote)
            }
    }
}