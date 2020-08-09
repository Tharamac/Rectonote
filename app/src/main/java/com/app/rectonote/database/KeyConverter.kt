package com.app.rectonote.database

import androidx.room.TypeConverter
import com.app.rectonote.Key
import java.util.*

class KeyConverters {
    @TypeConverter
    fun InttoKey(value: Int?): Key? {
        return value?.let { Key.fromInt(value) }
    }

    @TypeConverter
    fun KeytoInt(key : Key): Int? {
        return key.ordinal
    }
}