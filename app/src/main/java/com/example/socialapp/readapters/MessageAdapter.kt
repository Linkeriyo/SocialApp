package com.example.socialapp.readapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.R
import com.example.socialapp.models.Message
import com.example.socialapp.readapters.MessageAdapter.MessageViewHolder
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sendersUid == FirebaseAuth.getInstance().uid) {
            0
        } else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val v: View = if (viewType == 0) {
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_sent_row, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_received_row, parent, false)
        }
        return MessageViewHolder(v, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.contentTextView!!.text = messageList[position].content
        holder.timeTextView!!.text = Message.timeStringFromMillis(messageList[position].timeMillis)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class MessageViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        var contentTextView: TextView? = null
        var timeTextView: TextView? = null

        init {
            if (viewType == 0) {
                contentTextView = itemView.findViewById(R.id.sentMessageTextView)
                timeTextView = itemView.findViewById(R.id.sentMessageTime)
            } else {
                contentTextView = itemView.findViewById(R.id.receivedMessageTextView)
                timeTextView = itemView.findViewById(R.id.receivedMessageTime)
            }
        }
    }
}