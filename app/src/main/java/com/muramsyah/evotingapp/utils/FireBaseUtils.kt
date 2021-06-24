package com.muramsyah.evotingapp.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FireBaseUtils {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val ref: FirebaseDatabase = FirebaseDatabase.getInstance()
}