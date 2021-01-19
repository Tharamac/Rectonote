package com.app.rectonote.database

import androidx.room.TypeConverter
import com.app.rectonote.midiplayback.GeneralMidiPreset

class PresetConverters {
    @TypeConverter
    fun presetToStr(value: GeneralMidiPreset?): String {
        return value?.name ?: "null"
    }

    @TypeConverter
    fun strToPreset(presetName: String): GeneralMidiPreset? {
        return GeneralMidiPreset.values().find { it.name == presetName }
    }
}