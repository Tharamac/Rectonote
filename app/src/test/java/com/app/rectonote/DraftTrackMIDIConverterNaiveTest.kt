package com.app.rectonote

import com.app.rectonote.midiplayback.DraftTrackMIDIConverter
import com.app.rectonote.musictheory.DraftTrackData
import com.app.rectonote.musictheory.Key
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch.*
import kotlin.test.Test

class DraftTrackMIDIConverterNaiveTest {

    @Test
    @Throws(Exception::class)
    fun `test converting melody track into midi events`() {
        val melody = arrayListOf<Note>(
            Note(C, 4).apply {
                duration = 4
            },
            Note.restNote().apply {
                duration = 4
            },
            Note(D, 4).apply {
                duration = 2
            },
            Note(F, 4).apply {
                duration = 8
            },
            Note.restNote().apply {
                duration = 2
            },
            Note(D, 4).apply {
                duration = 1
            },
            Note(C, 4).apply {
                duration = 2
            },

            )
        val draftTrackData = DraftTrackData(
            key = Key.C,
            tempo = 60,
            trackType = "melody",
            trackSequence = melody
        )
        val result = DraftTrackMIDIConverter(draftTrackData).convert()
        println(result)

    }
}