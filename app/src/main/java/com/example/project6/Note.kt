package com.example.project6

import com.google.firebase.database.Exclude

data class Note(@get:Exclude
                var noteId: String = "",
                var noteName: String = "",
                var noteDescription: String = "")