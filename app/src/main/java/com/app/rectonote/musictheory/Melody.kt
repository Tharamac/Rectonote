package com.app.rectonote.musictheory

import com.app.rectonote.correlationCoefficient
import com.app.rectonote.leftShift
import kotlin.math.abs

class Melody(private val rawNotes: Array<NoteUnit>) : TrackSequence(rawNotes) {
    var melody = ArrayList<Note>()
    override fun generateTrack() {
        initTrack()
        melody.add(Note(rawNotes[frameStart].pitch, rawNotes[frameStart].octave))
        rawNotes.drop(frameStart + 1).forEach {
            if (it == melody.last()) {
                melody.last().lengthInFrame++
            } else {
                melody.add(Note(it.pitch, it.octave))
                updateSequence()
            }
        }
        // cleanTrack
    }

    override fun updateSequence() {
        val latestCompleteNote = melody[melody.lastIndex - 1]
        if (latestCompleteNote.lengthInFrame < 3) {
            if (abs(melody.last() - latestCompleteNote) > 1 || abs(melody.last() - latestCompleteNote) != 12) {
                if (melody.size < 3) {
                    melody[melody.lastIndex - 1].makeRestNote()
                } else {
                    melody[melody.lastIndex - 2] += latestCompleteNote.lengthInFrame
                    melody.removeAt(melody.lastIndex - 1)
                }
            }

        }
    }

    //Unit Test This
    override fun cleanTrack() {
        if (melody.first().pitch == NotePitch.REST) {
            melody.removeAt(0)
        }
        var adjacent = melody.zipWithNext().find { it.first == it.second }
        while (adjacent != null) {
            var adjacentIdx = melody.zipWithNext().indexOf(adjacent)
            melody[adjacentIdx].lengthInFrame += adjacent.second.lengthInFrame
            melody.removeAt(adjacentIdx + 1)
            adjacent = melody.zipWithNext().find { it.first == it.second }
        }
        if (melody.last().pitch == NotePitch.REST) {
            melody.removeAt(melody.lastIndex)
        }
    }

    override fun calcPitchProfile(): Array<Int> {
        var pitchProfile = Array<Int>(12) { 0 }
        melody.forEach {
            if (it.pitch != NotePitch.REST)
                pitchProfile[it.pitch.pitchNum] += it.lengthInFrame
        }
        return pitchProfile
    }

    override fun calcKey() {
        val majorProfile =
            doubleArrayOf(6.35, 2.23, 3.48, 2.33, 4.38, 4.09, 2.52, 5.19, 2.39, 3.66, 2.29, 2.88)
        val minorProfile =
            doubleArrayOf(6.33, 2.68, 3.52, 5.38, 2.60, 3.53, 2.54, 4.75, 3.98, 2.69, 3.34, 3.17)
        val pitchProfile = calcPitchProfile()
        var majorR = DoubleArray(12) { 0.00 }
        var minorR = DoubleArray(12) { 0.00 }
        for (i in majorR.indices) {
            majorR[i] = correlationCoefficient(majorProfile, pitchProfile)
            minorR[i] = correlationCoefficient(minorProfile, pitchProfile)
            pitchProfile.leftShift(1)
        }
        val maxMajorR = majorR.max() ?: -1.00
        val maxMajorRIdx = majorR.indices.maxBy { majorR[it] } ?: -1
        val maxMinorR = minorR.max() ?: -1.00
        val maxMinorRIdx = minorR.indices.maxBy { minorR[it] } ?: -1
        if (maxMajorR >= maxMinorR) {
            this.key = Key.values().find { key -> key.ordinal == maxMajorRIdx }!!
        } else {
            this.key = Key.values().find { key -> key.ordinal == maxMinorRIdx + 12 }!!
        }
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
        melody.forEach {
            quote += it.toString() + "\n"
        }
        return quote
    }
}