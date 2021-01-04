package com.app.rectonote.database

import androidx.room.TypeConverter
import com.app.rectonote.musictheory.Key

class KeyTypeConverter {
    @TypeConverter
    fun keyToStr(value: Key?): Int {
        return value?.ordinal ?: -1
    }

    @TypeConverter
    fun strToKey(reduced: Int): Key? {
        return Key.values().find { it.ordinal == reduced }
    }

}