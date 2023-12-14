package com.example.project6

import androidx.recyclerview.widget.DiffUtil
class NoteDiffItemCallback : DiffUtil.ItemCallback<Note>(){
    override fun areItemsTheSame(oldItem: Note, newItem: Note)
            = (oldItem.noteId == newItem.noteId)

    override fun areContentsTheSame(oldItem: Note, newItem: Note) = (oldItem == newItem)
}