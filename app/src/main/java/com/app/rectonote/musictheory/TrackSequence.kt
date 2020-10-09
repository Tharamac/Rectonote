package com.app.rectonote.musictheory

import android.util.Log

abstract class TrackSequence(private val rawNote: Array<NoteUnit>) {
    var key: Key? = null
    var tempo: Int = 0
    var lengthInFrame: Int = 0
    var trackDuration: Double = 0.00
    var frameStart = -1

    fun initTrack() {
        var i = 0
        while (rawNote[i].pitch == NotePitch.REST && i < rawNote.size) i++
        frameStart = i
        if (frameStart == rawNote.size) {
            Log.w("ZERO NOTE FOUND", "There are not any notes found.")

        }
    }

    abstract fun generateTrack()
    abstract fun updateSequence()
    abstract fun cleanTrack()
    abstract fun calcPitchProfile(): Array<Int>
    abstract fun calcKey()
    abstract fun calcDurations()
    abstract fun calcTempo()
}