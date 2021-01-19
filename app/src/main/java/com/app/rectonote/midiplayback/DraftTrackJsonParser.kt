package com.app.rectonote.midiplayback

import com.app.rectonote.musictheory.Chord
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon

class DraftTrackJsonParser {

    companion object {
        val draftTrackNoteJsonConverter = object : Converter {
            override fun canConvert(cls: Class<*>): Boolean = cls == Note::class.java
            override fun fromJson(jv: JsonValue): Any {
                return Note(
                    pitch = NotePitch.values().find { it.name == jv.objString("pitch") }
                        ?: NotePitch.REST,
                    octave = jv.objInt("octave")
                ).apply {
                    duration = jv.objInt("duration")
                }

            }

            override fun toJson(value: Any): String {
                return Klaxon().toJsonString(value)
            }

        }
        val draftTrackChordJsonConverter = object : Converter {
            override fun canConvert(cls: Class<*>): Boolean = cls == Chord::class.java
            override fun fromJson(jv: JsonValue): Any {
                return Chord(
                    pitch = NotePitch.values().find { it.pitchName == jv.objString("pitch") }
                        ?: NotePitch.REST,
                    octave = jv.objInt("octave")
                ).apply {
                    duration = jv.objInt("duration")
                    chordType = jv.objString("chordType")
                }

            }

            override fun toJson(value: Any): String {
                return Klaxon().toJsonString(value)
            }

        }

        fun draftTrackJSONParse(jsonString: String): ArrayList<out Note> {
            val trackList = if (jsonString.contains("chordType")) {
                Klaxon().converter(draftTrackChordJsonConverter).parseArray<Chord>(jsonString)
            } else
                Klaxon().converter(draftTrackNoteJsonConverter).parseArray<Note>(jsonString)
            val trackSequence = ArrayList<Note>()
            if (trackList != null)
                trackSequence.addAll(trackList)
            return trackSequence
        }
    }
}