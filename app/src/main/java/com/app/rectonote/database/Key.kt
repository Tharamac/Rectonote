package com.app.rectonote.database

enum class Key(val label: String, val reduced: String) {
    C("C major", "C"),
    Db("C# major", "C#"),
    D("D major", "D"),
    Eb("D# major", "D#"),
    E("E major", "E"),
    F("F major", "F"),
    Gb("F# major", "F#"),
    G("G major", "G"),
    Ab("G# major", "G#"),
    A("A major", "A"),
    Bb("A# major", "A#"),
    B("B major", "B"),
    Cm("C minor", "Cm"),
    Dbm("C# minor", "C#m"),
    Dm("D minor", "Dm"),
    Ebm("D# minor", "D#m"),
    Em("E minor", "Em"),
    Fm("F minor", "Fm"),
    Gbm("F# minor", "F#m"),
    Gm("G minor", "Gm"),
    Abm("G# minor", "G#m"),
    Am("A minor", "Am"),
    Bbm("A# minor", "A#m"),
    Bm("B minor", "Bm");

    //C, C#, D, D#, E, F, F#, G, G#, A, A#, B
    companion object {
        fun reduceKey(label: String): String {
            val key = Key.values().find { it.label == label }
            return key!!.reduced
        }

    }

}


