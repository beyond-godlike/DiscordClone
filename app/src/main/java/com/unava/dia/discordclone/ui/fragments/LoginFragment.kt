package com.unava.dia.discordclone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.unava.dia.discordclone.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class LoginFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    private var listener: LoginInteractionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btSubmit.setOnClickListener {
            loginUser()
        }

        if (isLoggedIn()) {
            this.listener?.onLoginClicked()
        }
    }

    private fun loginUser() {
        val email = etLogin.editText?.text.toString().trim()
        val password = etPassword.editText?.text.toString().trim()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        isLoggedIn()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun registerUser() {
        val email = etLogin.editText?.text.toString().trim()
        val password = etPassword.editText?.text.toString().trim()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withContext(Dispatchers.IO) {
                        auth.createUserWithEmailAndPassword(email, password).await()
                        withContext(Dispatchers.Main) {
                            isLoggedIn()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun isLoggedIn() : Boolean {
        return auth.currentUser != null
    }

    override fun onStart() {
        super.onStart()
        //isLoggedIn()
    }

    interface LoginInteractionListener {
        fun onLoginClicked()
    }
}