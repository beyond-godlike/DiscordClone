package com.unava.dia.discordclone.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.unava.dia.discordclone.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val firebaseRef: FirebaseDatabase) : ViewModel() {
    val users: MutableLiveData<List<User>> = MutableLiveData()
    val userId: MutableLiveData<Int> = MutableLiveData()

    var dbUsers = firebaseRef.reference.child("users")

    fun loadUsers() {
        dbUsers.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    collectUsernames(dataSnapshot.value as Map<String?, Any?>?)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
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