package com.app.rectonote.musictheory

import kotlin.math.abs

class ChordProgression(private val rawNotes: Array<Note>) : Melody(rawNotes) {

    private var chordProgression = ArrayList<Chord>()

    init {
        super.initTrack()
        generateTrack()
        cleanTrack()
        pitchProfile = calcPitchProfile()
        super.calcKey()
    }


    override fun generateTrack() {
        chordProgression.add(Chord(rawNotes[frameStart].pitch, rawNotes[frameStart].octave))
        rawNotes.drop(frameStart + 1).forEach {
            if (it == chordProgression.last()) {
                chordProgression.last().lengthInFrame++
            } else {
                chordProgression.add(Chord(it.pitch, it.octave))
                updateSequence()
            }
        }
    }

    override fun updateSequence() {
        val latestCompleteNote = chordProgression[chordProgression.lastIndex - 1]
        if (latestCompleteNote.lengthInFrame < 3) {
            if (abs(chordProgression.last() - latestCompleteNote) > 1 || abs(chordProgression.last() - latestCompleteNote) != 12) {
                if (chordProgression.size < 3) {
                    chordProgression[chordProgression.lastIndex - 1].makeRestNote()
                } else {
                    chordProgression[chordProgression.lastIndex - 2] += latestCompleteNote.lengthInFrame
                    chordProgression.removeAt(chordProgression.lastIndex - 1)
                }
            }

        }
    }

    //Unit Test This
    override fun cleanTrack() {
        if (chordProgression.first().pitch == NotePitch.REST) {
            chordProgression.removeAt(0)
        }
        var adjacent = chordProgression.zipWithNext().find { it.first == it.second }
        while (adjacent != null) {
            var adjacentIdx = chordProgression.zipWithNext().indexOf(adjacent)
            chordProgression[adjacentIdx].lengthInFrame += adjacent.second.lengthInFrame
            chordProgression.removeAt(adjacentIdx + 1)
            adjacent = chordProgression.zipWithNext().find { it.first == it.second }
        }
        if (chordProgression.last().pitch == NotePitch.REST) {
            chordProgression.removeAt(chordProgression.lastIndex)
        }
    }

    override fun calcPitchProfile(): Array<Int> {
        var pitchProfile = Array<Int>(12) { 0 }
        chordProgression.forEach {
            if (it.pitch != NotePitch.REST)
                pitchProfile[it.pitch.pitchNum] += it.lengthInFrame
        }
        return pitchProfile
    }


    override fun calcDurations() {
        TODO("Not yet implemented")
    }

    override fun calcTempo() {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        var quote = (key?.reduced ?: "")
        quote += "\n"
        chordProgression.forEach {
            quote += it.toString() + "\n"
        }
        return quote
    }
}