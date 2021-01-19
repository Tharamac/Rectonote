package com.app.rectonote.midiplayback

enum class GeneralMidiPreset(val bankNumber: Int, val programNumber: Int) {
    piano(0, 0),
    guitar(0, 24),
    bass(0, 32),
    violin(0, 40)
}