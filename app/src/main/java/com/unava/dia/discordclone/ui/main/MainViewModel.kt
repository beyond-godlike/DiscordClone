package com.unava.dia.discordclone.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unava.dia.discordclone.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val users: MutableLiveData<List<User>> = MutableLiveData()
    val userId: MutableLiveData<Int> = MutableLiveData()

    // get users from firebase
    fun loadUsers() {
        var u = ArrayList<User>()
        u.add(User("Andrei", "wer"))
        u.add(User("Invoker", "werg"))
        u.add(User("Ember", "wwefg"))
        users.postValue(u)
    }

    fun loadChat(id: Int) {

    }
}