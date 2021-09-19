package com.unava.dia.discordclone.ui.fragments.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {

    val error: MutableLiveData<String> = MutableLiveData()
    val registered: MutableLiveData<Boolean> = MutableLiveData()

    fun registerUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withContext(Dispatchers.IO) {
                        auth.createUserWithEmailAndPassword(email, password).await()
                        withContext(Dispatchers.Main) {
                            if (auth.currentUser != null) registered.postValue(true)
                            else registered.postValue(false)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        error.postValue(e.message)
                    }
                }
            }
        }
    }
}