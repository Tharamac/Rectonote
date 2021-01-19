package com.app.rectonote.midiplayback

import com.app.rectonote.musictheory.Note

data class TrackChannelStatus(
    val trackId: Int,
    var muted: Boolean,
    var preset: GeneralMidiPreset,
    val trackType: String,
    val trackSequence: ArrayList<out Note>
)
