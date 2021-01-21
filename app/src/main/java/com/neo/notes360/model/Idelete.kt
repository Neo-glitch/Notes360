package com.neo.notes360.model

import com.neo.notes360.dataSource.database.Note

interface Idelete {
    fun deleteSingleNote(note: Note)
}