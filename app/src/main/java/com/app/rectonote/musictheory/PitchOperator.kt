package com.app.rectonote.musictheory

open class PitchOperator {
    open fun intToNotePitch(pitchNum: Int): NotePitch =
        NotePitch.values().find { it.pitchNum == pitchNum } ?: NotePitch.REST

    open fun plusPitch(pitch: NotePitch, offset: Int): NotePitch =
        if (pitch != NotePitch.REST) intToNotePitch((pitch.pitchNum + offset) % 12) else NotePitch.REST
}