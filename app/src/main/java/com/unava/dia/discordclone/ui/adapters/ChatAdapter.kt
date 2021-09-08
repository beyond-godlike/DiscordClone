package com.unava.dia.discordclone.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.unava.dia.discordclone.R
import java.util.Collections.addAll

class ChatAdapter(private val messages: MutableList<String>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.messag_item, parent, false)
        view.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = getItem(position)
        holder.msg.text = message
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun getItem(position: Int) = messages[position]

    fun addMessages(newMessages: MutableList<String>) {
        val taskDiffUtil = MessageDiffUtil(messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(taskDiffUtil)
        messages.clear()
        messages.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }



    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.tvMessage)

    }
}
