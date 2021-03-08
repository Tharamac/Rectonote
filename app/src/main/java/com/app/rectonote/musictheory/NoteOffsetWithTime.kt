package com.app.rectonote.musictheory

class NoteOffsetWithTime(
    val timeSet: FloatArray,
    noteOffsetSet: IntArray
) {
    val rawNotes = Array<Note>(noteOffsetSet.size) {
        Note(noteOffsetSet[it], Note(NotePitch.C, 2))
    }
}

