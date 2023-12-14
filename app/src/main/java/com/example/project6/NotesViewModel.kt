package com.example.project6

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class NotesViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    var user: User = User()
    var verifyPassword = ""
    var noteId: String = ""
    var note = MutableLiveData<Note>()
    private val _notes: MutableLiveData<MutableList<Note>> = MutableLiveData()
    val notes: LiveData<List<Note>>
        get() = _notes as LiveData<List<Note>>
    private val _navigateToNote = MutableLiveData<String?>()
    val navigateToNote: LiveData<String?>
        get() = _navigateToNote

    private val _errorHappened = MutableLiveData<String?>()
    val errorHappened: LiveData<String?>
        get() = _errorHappened

    private val _navigateToList = MutableLiveData<Boolean>(false)
    val navigateToList: LiveData<Boolean>
        get() = _navigateToList

    private val _navigateToSignUp = MutableLiveData<Boolean>(false)
    val navigateToSignUp: LiveData<Boolean>
        get() = _navigateToSignUp

    private val _navigateToSignIn = MutableLiveData<Boolean>(false)
    val navigateToSignIn: LiveData<Boolean>
        get() = _navigateToSignIn

    private lateinit var notesCollection: DatabaseReference


    init {
        if (noteId.trim() == "") {
            note.value = Note()
        }
        _notes.value = mutableListOf<Note>()
        initializeTheDatabaseReference()

    }

    private fun initializeTheDatabaseReference() {

        if(auth.currentUser == null){
            return
        }
        val database = Firebase.database
        notesCollection = database
            .getReference("notes")
            .child(auth.currentUser!!.uid)


        notesCollection.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var notesList: ArrayList<Note> = ArrayList()
                for (noteSnapshot in dataSnapshot.children) {
                    var note = noteSnapshot.getValue<Note>()
                    note?.noteId = noteSnapshot.key!!
                    notesList.add(note!!)
                }
                _notes.value = notesList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("NotesViewModel", "Database Error")
            }
        })

    }

    fun getAll(): LiveData<List<Note>> {
        return notes
    }


    fun isUserLoggedIn(): Boolean{
        if(auth.currentUser != null){
            return true
        }
        else{
            return false
        }
    }
    fun updateNote() {
        if(auth.currentUser == null){
            return
        }
        if (noteId.trim() == "") {
            notesCollection.push().setValue(note.value)
        } else {
            notesCollection.child(noteId).setValue(note.value)
        }
        _navigateToList.value = true
    }

    fun deleteNote(noteId: String) {
        notesCollection.child(noteId).removeValue()
    }

    fun onNoteClicked(selectedNote: Note) {
        _navigateToNote.value = selectedNote.noteId
        noteId = selectedNote.noteId
        note.value = selectedNote
    }

    fun onNewNoteClicked() {
        _navigateToNote.value = ""
        noteId = ""
        note.value = Note()
    }

    fun onNoteNavigated() {
        _navigateToNote.value = null
    }

    fun onNavigatedToList() {
        _navigateToList.value = false
    }

    fun navigateToSignUp() {
        _navigateToSignUp.value = true
    }

    fun onNavigatedToSignUp() {
        _navigateToSignUp.value = false
    }

    fun navigateToSignIn() {
        _navigateToSignIn.value = true
    }

    fun onNavigatedToSignIn() {
        _navigateToSignIn.value = false
    }

    fun signIn() {
        if (user.email.isEmpty() || user.password.isEmpty()) {
            _errorHappened.value = "Email and password cannot be empty."
            return
        }
        auth.signInWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if (it.isSuccessful) {
                initializeTheDatabaseReference()
                _navigateToList.value = true
            } else {
                _errorHappened.value = it.exception?.message
            }
        }
    }

    fun signUp() {
        if (user.email.isEmpty() || user.password.isEmpty()) {
            _errorHappened.value = "Email and password cannot be empty."
            return
        }
        if (user.password != verifyPassword) {
            _errorHappened.value = "Password and verify do not match."
            return
        }
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if (it.isSuccessful) {
                _navigateToSignIn.value = true
            } else {
                _errorHappened.value = it.exception?.message
            }
        }
    }

    fun signOut() {
        auth.signOut()

        _navigateToSignIn.value = true
    }


    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}