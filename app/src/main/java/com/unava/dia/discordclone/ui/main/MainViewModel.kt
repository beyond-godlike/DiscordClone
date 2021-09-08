package com.unava.dia.discordclone.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.unava.dia.discordclone.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener





@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val users: MutableLiveData<List<User>> = MutableLiveData()
    val userId: MutableLiveData<Int> = MutableLiveData()

    val messages: MutableLiveData<List<String>> = MutableLiveData()

    // get users from firebase
    //val ref = FirebaseDatabase.getInstance().reference.child("users")
    var firebaseRef =
        FirebaseDatabase.getInstance("https://discordclone-dd303-default-rtdb.europe-west1.firebasedatabase.app/")
    var dbUsers = firebaseRef.reference.child("users")
    var dbMessages = firebaseRef.reference.child("messages")

    fun postMessage(msg: String) {
        dbMessages.push().setValue(msg)
    }

    fun fetchMessages() {dbMessages.addListenerForSingleValueEvent(
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                collectMessges(dataSnapshot.value as Map<Any?, String?>?)
            }

            override fun onCancelled(databaseError: DatabaseError) { }
        })
    }
    fun collectMessges(u: Map<Any?, String?>?) {
        val us = ArrayList<String>()
        for ((key, value) in u?.entries!!) {
            us.add(value as String)
        }
        messages.postValue(us)
    }
    fun loadUsers() {
        dbUsers.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    collectUsernames(dataSnapshot.value as Map<String?, Any?>?)
                }

                override fun onCancelled(databaseError: DatabaseError) { }
            })
    }

    private fun collectUsernames(u: Map<String?, Any?>?) {
        val us = ArrayList<User>()
        for ((key, value) in u?.entries!!) {
            us.add(User(key as String))
        }
        users.postValue(us)
    }

    fun loadChat(id: Int) {

    }

    fun changeCurrentUserId(it: Int) {
        userId.postValue(it)
    }
}