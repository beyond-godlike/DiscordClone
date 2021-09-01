package com.unava.dia.discordclone.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unava.dia.discordclone.R
import com.unava.dia.discordclone.data.User
import kotlinx.android.synthetic.main.model_navigator.view.*

class UsersAdapter(
    private val users: List<User>
    ) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
    var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.model_navigator, parent, false) as View
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.position = position
        //PicassoUtils.setImageHero(
        //    holder.heroImage,
        //    heroes[position]!!.rname,
        //    ProjectConstants.IMAGE_HERO_SMALL_WIDTH, ProjectConstants.IMAGE_HERO_SMALL_HEIGHT
        //)
        holder.userName.text = users[position]?.name ?: "no data"
    }

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var position: Int? = null
        var userImage: ImageView = itemView.ivUser
        var userName: TextView = itemView.tvUserName
    }
}