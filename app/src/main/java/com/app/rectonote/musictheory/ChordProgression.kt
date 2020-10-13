package com.app.rectonote.musictheory

import android.util.Log
import com.app.rectonote.correlationCoefficient
import com.app.rectonote.leftShift
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class ChordProgression(private val rawNotes: Array<Note>) {

    var key: Key? = null
    private var tempo: Int = 0
    private var frameStart = -1
    private var pitchProfile: Array<Int>
    private var chordProgression = ArrayList<Chord>()

    init {
        initTrack()
        generateTrack()
        cleanTrack()
        pitchProfile = calcPitchProfile()
        calcKey()
        calcDurations()
        calcTempo(0.04)
    }

    private fun initTrack() {
        var i = 0
        while (rawNotes[i].pitch == NotePitch.REST && i < rawNotes.size) i++
        frameStart = i
        if (frameStart == rawNotes.size) {
            Log.w("ZERO NOTE FOUND", "There are not any notes found.")

        }
    }

    private fun generateTrack() {
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

    private fun updateSequence() {
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
    private fun cleanTrack() {
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

    private fun calcPitchProfile(): Array<Int> {
        var pitchProfile = Array<Int>(12) { 0 }
        chordProgression.forEach {
            if (it.pitch != NotePitch.REST)
                pitchProfile[it.pitch.pitchNum] += it.lengthInFrame
        }
        return pitchProfile
    }

    private fun calcKey() {
        val majorProfile =
            doubleArrayOf(6.35, 2.23, 3.48, 2.33, 4.38, 4.09, 2.52, 5.19, 2.39, 3.66, 2.29, 2.88)
        val minorProfile =
            doubleArrayOf(6.33, 2.68, 3.52, 5.38, 2.60, 3.53, 2.54, 4.75, 3.98, 2.69, 3.34, 3.17)
        var majorR = DoubleArray(12) { 0.00 }
        var minorR = DoubleArray(12) { 0.00 }
        for (i in majorR.indices) {
            majorR[i] = correlationCoefficient(majorProfile, pitchProfile)
            minorR[i] = correlationCoefficient(minorProfile, pitchProfile)
            pitchProfile.leftShift(1)
        }
        val maxMajorR = majorR.maxOrNull() ?: -1.00
        val maxMajorRIdx = majorR.indices.maxByOrNull { majorR[it] } ?: -1
        val maxMinorR = minorR.maxOrNull() ?: -1.00
        val maxMinorRIdx = minorR.indices.maxByOrNull { minorR[it] } ?: -1
        if (maxMajorR >= maxMinorR) {
            this.key = Key.values().find { key -> key.ordinal == maxMajorRIdx }!!
        } else {
            this.key = Key.values().find { key -> key.ordinal == maxMinorRIdx + 12 }!!
        }
    }


    private fun calcDurations() {
        val minFrame = chordProgression.minByOrNull { it.lengthInFrame }?.lengthInFrame ?: -1
        //lowest unit of note duration is a sixteenth note, so that duration 1 is a sixteenth note
        //a scale variable is use to scale whole duration in order to keep tempo between around 60 - 180 bpm
        val scale = minFrame / 13
        chordProgression.forEach {
            it.duration =
                (it.lengthInFrame.toDouble() / minFrame).roundToInt() * (2.00.pow(scale).toInt())
        }
        //possible least duration is 1 2 4 8 and so on...
    }

    private fun calcTempo(frameSize: Double) {
        val minFrame = chordProgression.minByOrNull { it.lengthInFrame }!!
        val minDuration = minFrame.duration
        val minLength = minFrame.lengthInFrame
        val blackNoteFrameLength = minLength * 2.00.pow(3.0 - sqrt(minDuration.toFloat()))
        val blackNoteInSecond = (blackNoteFrameLength * frameSize * 0.5) + (frameSize * 0.5)
        tempo = (60 / blackNoteInSecond).roundToInt()
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