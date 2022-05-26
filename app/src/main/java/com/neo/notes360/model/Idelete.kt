package com.neo.notes360.model

import com.neo.notes360.dataSource.database.Note


@FunctionalInterface
interface Idelete {
    fun deleteSingleNote(note: Note)
}