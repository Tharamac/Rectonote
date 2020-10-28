package com.app.rectonote.musictheory

enum class NotePitch(val pitchNum: Int, val pitchName: String) {
    C(0, "C"),
    Db(1, "C#"),
    D(2, "D"),
    Eb(3, "D#"),
    E(4, "E"),
    F(5, "F"),
    Gb(6, "F#"),
    G(7, "G"),
    Ab(8, "G#"),
    A(9, "A"),
    Bb(10, "A#"),
    B(11, "B"),
    REST(-1, "-");

    companion object {
        fun intToNotePitch(pitchNum: Int): NotePitch =
            NotePitch.values().find { it.pitchNum == pitchNum } ?: REST

        fun plusPitch(pitch: NotePitch, offset: Int): NotePitch =
            if (pitch != REST) intToNotePitch((pitch.pitchNum + offset) % 12) else REST
    }
}