package com.neo.notes360.database

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {

    // take long value and ret date, for showing to user(not used in this app though)
    @TypeConverter
    fun toDate(value: Long): Date{
        return Date(value)
    }


    // takes date value and ret longs, for saving in db
    @TypeConverter
    fun toLong(value: Date): Long{
        return value.time
    }
}