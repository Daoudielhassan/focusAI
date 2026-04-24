package com.focus.mob.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository(private val auth: FirebaseAuth) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun signOut() {
        auth.signOut()
    }
    
    fun getAuthInstance(): FirebaseAuth = auth
}
