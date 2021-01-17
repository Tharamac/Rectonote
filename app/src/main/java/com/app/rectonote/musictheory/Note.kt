package com.app.rectonote.musictheory

import com.beust.klaxon.Json

open class Note(
    var pitch: NotePitch = NotePitch.REST,
    var octave: Int = -1,

    ) {
    @Json(ignored = true)
    var lengthInFrame = 1
    var duration = -1


    constructor(offset: Int, refNote: Note) : this(refNote.pitch, refNote.octave) {
        this.plusAssign(offset)
    }

    //unit test this

    operator fun plusAssign(offset: Int) {
        if (this.pitch != NotePitch.REST) {
            val change = (this.octave * 12 + this.pitch.pitchNum) + offset
            this.octave = change / 12
            this.pitch = PitchOperator().intToNotePitch(change % 12)
        }

    }

    override operator fun equals(other: Any?): Boolean = if (other !is Note) false
    else (this.pitch == other.pitch) && (this.octave == other.octave) && (this.duration == other.duration) && (this.lengthInFrame == other.lengthInFrame)

    operator fun minus(b: Note): Int {
        val octaveDiff = (this.octave - b.octave) * 12
        val pitchDiff = this.pitch.pitchNum - b.pitch.pitchNum
        return octaveDiff + pitchDiff
    }

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
                if (offsetArray[i] == -999)
                    Note(NotePitch.REST, -1)
                else
                    Note(offsetArray[i], refNote)
            }

        fun restNote(): Note = Note(NotePitch.REST, -1)
    }


}