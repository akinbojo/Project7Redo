package com.example.project6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.project6.databinding.FragmentEditNoteBinding

class EditNoteFragment : Fragment() {
    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        val view = binding.root
        val args = requireArguments()
        val noteId = args.getString("NoteId")
        val application = requireNotNull(this.activity).application

        val viewModel : NotesViewModel by activityViewModels()
        binding.lifecycleOwner = viewLifecycleOwner
        if (noteId != null) {
            viewModel.noteId = noteId
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.navigateToList.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                view.findNavController()
                    .navigate(R.id.action_editNoteFragment_to_notesFragment)
                viewModel.onNavigatedToList()
            }
        })

        binding.deleteNoteBtn.setOnClickListener {
            viewModel.deleteNote(viewModel.noteId)
            view.findNavController()
                .navigate(R.id.action_editNoteFragment_to_notesFragment)
            viewModel.onNavigatedToList()
        }
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}