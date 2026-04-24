package com.focus.mob.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.focus.mob.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    fun signOut() {
        repository.signOut()
    }
    
    fun getAuthInstance(): FirebaseAuth = repository.getAuthInstance()
}
