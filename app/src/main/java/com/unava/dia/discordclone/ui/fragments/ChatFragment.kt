package com.unava.dia.discordclone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.unava.dia.discordclone.R
import com.unava.dia.discordclone.ui.adapters.ChatAdapter
import com.unava.dia.discordclone.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chat.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    private var chatAdapter: ChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        chat_btn_send_message.setOnClickListener {
            val msg = tvMessage.text.toString().trim()
            viewModel.postMessage(msg)
            tvMessage.text.clear()
        }
        viewModel.fetchMessages()
    }

    private fun observeViewModel() {
        viewModel.messages.observe(requireActivity(), {
            updateMessages(it)
        })
    }

    private fun updateMessages(list: List<String>) {
        if (list.isNotEmpty()) {
            if (chatAdapter == null) {
                chatAdapter =
                    ChatAdapter(list.toMutableList())
                rvChat.adapter = chatAdapter
                chatAdapter?.addMessages(list.toMutableList())
            }
        }
    }

    private fun setupRecyclerView() = rvChat.apply {
        layoutManager = LinearLayoutManager(requireContext())
    }
}