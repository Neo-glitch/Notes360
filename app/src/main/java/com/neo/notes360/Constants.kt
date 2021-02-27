package com.neo.notes360

class Constants {

    companion object{
        const val NOTE_TYPE = "note_type"
        const val NEW_NOTE_INTENT = "new_note"
        const val EDIT_NOTE_INTENT = "edit_note"
        const val NEW_NOTE = 0
        const val EDIT_NOTE = 1
        const val NOTE_ID = "note_id"
        const val NOTE_TITLE = "note_title"
        const val NOTE_CONTENT = "note_content"

        // firebase firestore storage constants
        const val NOTES_360 = "notes_360"
        const val NOTES = "notes"
        const val LAST_UPDATED = "last_updated"

        // local storage constants
        const val TABLE_NAME = "notes_table"
        const val NOTE_TITLE_COLUMN = "note_title"
        const val NOTE_CONTENT_COLUMN = "note_content"
        const val NOTE_LAST_UPDATED_COLUMN = "last_updated"
        const val DATABASE_NAME = "note_database"
    }
}