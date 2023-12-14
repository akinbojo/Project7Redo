package com.example.project6

import  android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.project6.databinding.ItemNoteCardBinding

class NoteViewHolder(private val binding: ItemNoteCardBinding,
                     private val navController: NavController) :
    RecyclerView.ViewHolder(binding.root) {



    fun bind(note: Note) {
        binding.apply {
            noteName.text = note.noteName
            noteDescription.text = note.noteDescription
        }

        binding.root.setOnClickListener {
            val action = NotesFragmentDirections.actionNotesFragmentToEditNoteFragment(note.noteId)
            navController.navigate(action)
        }


    }
}