package com.unava.dia.discordclone.ui.fragments.login

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
class LoginViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {

    val error: MutableLiveData<String> = MutableLiveData()
    val success: MutableLiveData<Boolean> = MutableLiveData()

    fun loginUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        if(auth.currentUser != null) success.postValue(true)
                        else success.postValue(false)
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