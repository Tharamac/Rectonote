package com.app.rectonote.musictheory

import android.util.Log
import com.app.rectonote.correlationCoefficient
import com.app.rectonote.leftShift
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class TrackSequencer(private val rawNotes: Array<Note>) {


    fun initTrack(): Int {
        var i = 0
        while (i < rawNotes.size && rawNotes[i].pitch == NotePitch.REST) i++
        if (i == rawNotes.size) {
            Log.w("ZERO NOTE FOUND", "There are not any notes found.")
            return -1
        }
        return i
    }

    fun generateTrack(convertType: String): ArrayList<out Note> {
        var frameStart = initTrack()
        var melody = ArrayList<Note>()
        if (convertType.toLowerCase() == "melody") {
            melody.add(Note(rawNotes[frameStart].pitch, rawNotes[frameStart].octave))
            rawNotes.drop(frameStart + 1).forEach {
                if (it == melody.last()) {
                    melody.last().lengthInFrame++
                } else {
                    melody.add(Note(it.pitch, it.octave))
                    //updateSequence()
                }
            }
        } else {
            melody.add(Chord(rawNotes[frameStart].pitch, rawNotes[frameStart].octave))
            rawNotes.drop(frameStart + 1).forEach {
                if (it == melody.last()) {
                    melody.last().lengthInFrame++
                } else {
                    melody.add(Chord(it.pitch, it.octave))
                    //updateSequence()
                }
            }
        }

        return melody
    }

    fun removeNoise(melody: ArrayList<out Note>): ArrayList<out Note> {
        var noiseNote = melody.indexOfFirst {
            it.lengthInFrame <= 3
        }
        while (noiseNote != -1) {
            var left = try {
                melody[noiseNote - 1]
            } catch (e: ArrayIndexOutOfBoundsException) {
                Note.restNote()
            }
            var current = melody[noiseNote]
            var right = try {
                melody[noiseNote + 1]
            } catch (e: IndexOutOfBoundsException) {
                Note.restNote()
            }
            if (left != Note.restNote()) {
                left.lengthInFrame += current.lengthInFrame
                if (left == right) {
                    left.lengthInFrame += right.lengthInFrame
                    try {
                        melody.removeAt(noiseNote + 1)
                    } catch (e: IndexOutOfBoundsException) {
                    }
                }
                melody.removeAt(noiseNote)
            } else if (left == Note.restNote()) {
                if (left == right) {
                    left.lengthInFrame += (current.lengthInFrame + right.lengthInFrame)
                    try {
                        melody.removeAt(noiseNote + 1)
                    } catch (e: IndexOutOfBoundsException) {
                    }
                    melody.removeAt(noiseNote)
                } else {
                    right.lengthInFrame += current.lengthInFrame
                    melody.removeAt(noiseNote)
                }
            }
            noiseNote = melody.indexOfFirst {
                it.lengthInFrame <= 3
            }
        }
        return melody
    }

    //Unit Test This
    fun cleanTrack(melody: ArrayList<out Note>): ArrayList<out Note> {
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
        if (melody.last().pitch == NotePitch.REST || melody.last().lengthInFrame < 3) {
            melody.removeAt(melody.lastIndex)
        }
        return melody
    }

    fun calcPitchProfile(melody: ArrayList<out Note>): Array<Int> {
        var pitchProfile = Array<Int>(12) { 0 }
        melody.forEach {
            if (it.pitch != NotePitch.REST)
                pitchProfile[it.pitch.pitchNum] += it.lengthInFrame
        }
        return pitchProfile
    }

    fun calcKey(pitchProfile: Array<Int>): Key {
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
        return if (maxMajorR >= maxMinorR) {
            Key.values().find { key -> key.ordinal == maxMajorRIdx }!!
        } else {
            Key.values().find { key -> key.ordinal == maxMinorRIdx + 12 }!!
        }
    }

    fun calcDurations(melody: ArrayList<out Note>): ArrayList<out Note> {
        val minFrame = melody.minByOrNull { it.lengthInFrame }?.lengthInFrame ?: -1
        //lowest unit of note duration is a sixteenth note, so that duration 1 is a sixteenth note
        //a scale variable is use to scale whole duration in order to keep tempo between around 60 - 180 bpm
        val scale = minFrame / 13
        melody.forEach {
            it.duration =
                (it.lengthInFrame.toDouble() / minFrame).roundToInt() * (2.00.pow(scale).toInt())
        }
        //possible least duration is 1 2 4 8 and so on...
        return melody
    }

    fun calcTempo(melody: ArrayList<out Note>, frameSize: Double): Int {
        val minFrame = melody.minByOrNull { it.lengthInFrame }!!
        val minDuration = minFrame.duration
        val minLength = minFrame.lengthInFrame
        val blackNoteFrameLength = minLength * 2.00.pow(3.0 - sqrt(minDuration.toFloat()))
        val blackNoteInSecond = (blackNoteFrameLength * frameSize * 0.5) + (frameSize * 0.5)
        return (60 / blackNoteInSecond).roundToInt()
    }

    fun chordCorrect(chordProg: ArrayList<out Note>, key: Key): ArrayList<out Note> {
        val rootNote = NotePitch.intToNotePitch(key.ordinal % 12)
        val minorChord = if (key.reduced.contains("m")) {
            //minor
            arrayOf(rootNote, NotePitch.plusPitch(rootNote, 5), NotePitch.plusPitch(rootNote, 7))
        } else {
            arrayOf(
                NotePitch.plusPitch(rootNote, 2),
                NotePitch.plusPitch(rootNote, 4),
                NotePitch.plusPitch(rootNote, 9)
            ) //major
        }
        chordProg.forEach {
            if (it is Chord) {
                if (it.pitch in minorChord) {
                    it.chordType = "Minor"
                }
            }
        }
        return chordProg
    }

}