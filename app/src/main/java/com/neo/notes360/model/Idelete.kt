package com.neo.notes360.model

import com.neo.notes360.database.Note

interface Idelete {
    fun deleteSingleNote(note: Note)
}