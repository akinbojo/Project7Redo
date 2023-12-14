package com.example.project6

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.project6.databinding.FragmentNotesBinding
import com.example.project6.databinding.ItemNoteCardBinding

class NotesFragment : Fragment() {
    val TAG = "NotesFragment"
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        val view = binding.root
        val viewModel : NotesViewModel by activityViewModels()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner



        fun newNoteClicked(){
            viewModel.onNewNoteClicked()
        }

        fun noteClicked (note : Note) {
            viewModel.onNoteClicked(note)
        }

        fun yesPressed(noteId : String) {
            Log.d(TAG, "in yesPressed(): noteId = $noteId")
            //TODO: delete the task with id = noteId
            binding.viewModel?.deleteNote(noteId)
        }
        fun deleteClicked (noteId : String) {
            ConfirmDeleteDialogFragment(noteId,::yesPressed).show(childFragmentManager,
                ConfirmDeleteDialogFragment.TAG)
        }

        class NoteAdapter : ListAdapter<Note, NoteViewHolder>(object : DiffUtil.ItemCallback<Note>() {

            override fun areItemsTheSame(oldItem: Note, newItem: Note) =
                oldItem.noteId == newItem.noteId

            override fun areContentsTheSame(oldItem: Note, newItem: Note) =
                oldItem == newItem

        }) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val binding = ItemNoteCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return NoteViewHolder(binding, findNavController())
            }

            override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
                val note = getItem(position)
                holder.bind(note)
            }

        }



        binding.notesList.adapter = NoteAdapter()
        var adapter: NoteAdapter = binding.notesList.adapter as NoteAdapter
        binding.notesList.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)

        viewModel.notes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })



        binding.addNoteBtn.setOnClickListener {
            val args = Bundle()
            val newNote = viewModel.onNewNoteClicked()
        }

        if(!viewModel.isUserLoggedIn()) {
            val action = NotesFragmentDirections
                .actionNotesFragmentToSignInFragment()
            findNavController().navigate(action)
        }
        viewModel.navigateToNote.observe(viewLifecycleOwner, Observer { noteId ->
            noteId?.let {
                val action = NotesFragmentDirections
                    .actionNotesFragmentToEditNoteFragment(noteId)
                this.findNavController().navigate(action)
                viewModel.onNoteNavigated()
            }
        })

        binding.userBtn.setOnClickListener {
            this.findNavController().navigate(R.id.action_notesFragment_to_signInFragment)
            viewModel.onNavigatedToSignIn()
        }
        viewModel.navigateToSignIn.observe(viewLifecycleOwner, Observer { navigate ->
            if(navigate) {
                this.findNavController().navigate(R.id.action_notesFragment_to_signInFragment)
                viewModel.onNavigatedToSignIn()
            }
        })

        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}