package com.example.firestorecoroutines.Objects

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Constants {
    val reference: DocumentReference = Firebase.firestore.collection("Users").document("User1")
}