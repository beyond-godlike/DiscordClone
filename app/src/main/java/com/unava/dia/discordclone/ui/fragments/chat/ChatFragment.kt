package com.unava.dia.discordclone.ui.fragments.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.unava.dia.discordclone.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chat.*


@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {

    private val viewModel: ChatViewModel by viewModels()

    private var chatAdapter: ChatAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        btSend.setOnClickListener {
            val msg = etMessage.text.toString().trim()
            viewModel.postMessage(msg)
            etMessage.text?.clear()
        }
        viewModel.fetchMessages()
    }

    private fun observeViewModel() {
        viewModel.messages.observe(requireActivity(), {
            updateMessages(it)
            rvChat.smoothScrollToPosition(it.size)
        })
    }

    private fun updateMessages(list: ArrayList<String>) {
        if (list.isNotEmpty()) {
            if (chatAdapter == null) {
                chatAdapter =
                    ChatAdapter(list.toMutableList())
                rvChat.adapter = chatAdapter
            }
            chatAdapter?.addMessages(list.toMutableList())
        }
    }

    private fun setupRecyclerView() = rvChat.apply {
        layoutManager = LinearLayoutManager(requireContext())
    }
}