package com.unava.dia.discordclone.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.unava.dia.discordclone.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseRef: FirebaseDatabase,
    private val auth: FirebaseAuth
) : ViewModel() {
    val users: MutableLiveData<List<User>> = MutableLiveData()
    val userId: MutableLiveData<Int> = MutableLiveData()

    var dbUsers = firebaseRef.reference.child("users")

    fun loadUsers() {
        dbUsers.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val us = ArrayList<User>()
                    //collectUsernames(dataSnapshot.value as Map<String?, Any?>?)
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val name = userSnapshot.child("name").value as String?
                            val roomState = userSnapshot.child("roomState").value as String?
                            val uid = userSnapshot.child("uid").value as Long?
                            val u = User(name, uid?.toInt(), roomState, ArrayList<String>())
                            us.add(u)
                        }
                    }

                    users.postValue(us)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun collectUsernames(u: Map<String?, Any?>?) {
        val us = ArrayList<User>()
        for ((key, value) in u?.entries!!) {
            //us.add(User(value))
        }
        //users.postValue(us)
    }

    fun loadChat(id: Int) {

    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    fun logOut() {
        auth.signOut()
    }

    fun changeCurrentUserId(it: Int) {
        userId.postValue(it)
    }
}