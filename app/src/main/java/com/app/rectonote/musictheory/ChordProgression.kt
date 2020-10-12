package com.app.rectonote.musictheory

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

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

    override fun calcTempo(frameSize: Double) {
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