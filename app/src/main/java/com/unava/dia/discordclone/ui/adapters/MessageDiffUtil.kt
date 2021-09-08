package com.unava.dia.discordclone.ui.adapters

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil

class MessageDiffUtil(private var oldTaskList: List<String>, private var newTaskList: List<String>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTaskList[oldItemPosition] === newTaskList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTaskList[oldItemPosition] == newTaskList[newItemPosition]
    }

    override fun getNewListSize(): Int {
        return newTaskList.size
    }

    override fun getOldListSize(): Int {
        return oldTaskList.size
    }


    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}