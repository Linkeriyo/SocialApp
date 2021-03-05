package com.example.socialapp.readapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.models.User
import com.example.socialapp.readapters.ContactsAdapter.ContactViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularimageview.CircularImageView

class ContactsAdapter(
        private val userList: List<User>,
        private val context: Context,
        private val chatClickListener: OnChatClickListener
        ) : RecyclerView.Adapter<ContactViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return if (userList[position].uid == FirebaseAuth.getInstance().uid) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_row, parent, false)
        if (viewType == 0) {
            v.visibility = View.GONE
            v.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
        return ContactViewHolder(v, chatClickListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val user = userList[position]
        Glide.with(context).load(user.image).into(holder.profilePicView)
        holder.nickView.text = user.nick
        holder.nameView.text = user.name
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ContactViewHolder(itemView: View, private val chatClickListener: OnChatClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val profilePicView: CircularImageView = itemView.findViewById(R.id.profilePicView)
        val nameView: TextView = itemView.findViewById(R.id.nameView)
        val nickView: TextView = itemView.findViewById(R.id.nickView)

        override fun onClick(v: View) {
            chatClickListener.onChatClick(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface OnChatClickListener {
        fun onChatClick(position: Int)
    }
}