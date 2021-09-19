package com.unava.dia.discordclone.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.unava.dia.discordclone.R
import com.unava.dia.discordclone.other.DrawerLocker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private var listener: LoginInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as DrawerLocker).setDrawerLocked(true)
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btSubmit.setOnClickListener {
            this.viewModel.loginUser(
                etLogin.editText?.text.toString().trim(),
                etPassword.editText?.text.toString().trim()
            )
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.success.observe(requireActivity(), {
            if (it) this.listener?.onLoginClicked()
        })
        viewModel.error.observe(requireActivity(), {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as DrawerLocker).setDrawerLocked(false)
    }

    interface LoginInteractionListener {
        fun onLoginClicked()
    }
}