package com.unava.dia.discordclone.ui.fragments.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtm.RtmClientListener
import io.agora.rtm.RtmMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ChatViewModel @Inject constructor(private val firebaseRef: FirebaseDatabase) : ViewModel() {

    val messages: MutableLiveData<ArrayList<String>> = MutableLiveData()

    var dbMessages = firebaseRef.reference.child("messages")

    fun postMessage(msg: String) {
        var key = System.currentTimeMillis().toString()
        dbMessages.child(key).setValue(msg)
        //dbMessages.push().setValue(msg)
    }

    fun fetchMessages() {
        if(dbMessages.key == null) return
        dbMessages.orderByValue().addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    collectMessges(dataSnapshot.value as Map<Any?, String?>?)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun collectMessges(u: Map<Any?, String?>?) {
        val us = ArrayList<String>()
        for ((key, value) in u?.entries!!) {
            us.add(value as String)
        }
        messages.postValue(us)
    }
}