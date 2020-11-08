package com.app.rectonote.musictheory

import com.app.rectonote.correlationCoefficient
import com.app.rectonote.leftShift
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

open class TrackSequencer(
    private val comparator: Equal = Equal(),
    private val pitchOperator: PitchOperator = PitchOperator()
) {
    open fun initTrack(rawNotes: Array<Note>): Int {
        var i = 0
        while (i < rawNotes.size && rawNotes[i].pitch == NotePitch.REST) i++
        if (i == rawNotes.size) {
            return -1
        }
        return i
    }

    fun generateTrack(rawNotes: Array<Note>, convertType: String): ArrayList<out Note> {
        val frameStart = initTrack(rawNotes)
        val melody = ArrayList<Note>()
        if (frameStart == -1) {
            return melody
        }
        if (convertType.toLowerCase(Locale.ROOT) == "melody") {
            melody.add(Note(rawNotes[frameStart].pitch, rawNotes[frameStart].octave))
            rawNotes.drop(frameStart + 1).forEach {
                if (comparator.isSame(it, melody.last())) {
                    melody.last().lengthInFrame++
                } else {
                    melody.add(Note(it.pitch, it.octave))
                    //updateSequence()
                }
            }
        } else {
            melody.add(Chord(rawNotes[frameStart].pitch, rawNotes[frameStart].octave))
            rawNotes.drop(frameStart + 1).forEach {
                if (comparator.isSame(it, melody.last())) {
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
            if (!comparator.isSame(left, Note.restNote())) {
                left.lengthInFrame += current.lengthInFrame
                if (comparator.isSame(left, right)) {
                    left.lengthInFrame += right.lengthInFrame
                    try {
                        melody.removeAt(noiseNote + 1)
                    } catch (e: IndexOutOfBoundsException) {

                    }
                }
                melody.removeAt(noiseNote)
            } else if (comparator.isSame(left, Note.restNote())) {
                if (comparator.isSame(left, right)) {
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
        var adjacent = melody.zipWithNext().find { comparator.isSame(it.first, it.second) }
        while (adjacent != null) {
            var adjacentIdx = melody.zipWithNext().indexOf(adjacent)
            melody[adjacentIdx].lengthInFrame += adjacent.second.lengthInFrame
            melody.removeAt(adjacentIdx + 1)
            adjacent = melody.zipWithNext().find { comparator.isSame(it.first, it.second) }
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
        val rootNote = pitchOperator.intToNotePitch(key.ordinal % 12)
        val minorChord = if (key.reduced.contains("m")) {
            //minor
            arrayOf(
                rootNote,
                pitchOperator.plusPitch(rootNote, 5),
                pitchOperator.plusPitch(rootNote, 7)
            )
        } else {
            arrayOf(
                pitchOperator.plusPitch(rootNote, 2),
                pitchOperator.plusPitch(rootNote, 4),
                pitchOperator.plusPitch(rootNote, 9)
            ) //major
        }
        chordProg.forEach {
            if (it is Chord) {
                if (it.pitch in minorChord) {
                    it.chordType = "minor"
                }
            }
        }
        return chordProg
    }

}