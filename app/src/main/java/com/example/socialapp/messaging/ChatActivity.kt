package com.example.socialapp.messaging

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.R
import com.example.socialapp.databinding.ActivityChatBinding
import com.example.socialapp.models.Message
import com.example.socialapp.readapters.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import java.util.function.Consumer

class ChatActivity : AppCompatActivity() {
    private var chatKey: String? = null
    private var otherUserNick: String? = null
    private lateinit var recyclerView: RecyclerView
    var messageList: MutableList<Message> = ArrayList()
    private var chatReference: DatabaseReference? = null
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        chatKey = intent.getStringExtra("chatKey")
        otherUserNick = intent.getStringExtra("otherUserNick")
        loadMessages()
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        recyclerView = binding.chatRecyclerView
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = MessageAdapter(messageList)

        val sendButton = binding.sendButton
        sendButton.setOnClickListener {
            val messageTextView = binding.messageTextView
            if (messageTextView.text.toString().isNotEmpty()) {
                chatReference!!.child(System.currentTimeMillis().toString() + "-" + FirebaseAuth.getInstance().uid)
                        .setValue(messageTextView.text.toString())
                messageTextView.setText("")
            }
        }
        val toolbar = findViewById<Toolbar>(R.id.chat_toolbar)
        toolbar.title = otherUserNick
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadMessages() {
        chatReference = FirebaseDatabase.getInstance().getReference("chats/$chatKey")
        chatReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                val messages = ArrayList<Message>()
                snapshot.children.forEach(Consumer { child: DataSnapshot -> messages.add(Message(child.key!!, child.getValue(String::class.java)!!)) })
                messageList.addAll(messages)
                recyclerView.adapter!!.notifyDataSetChanged()
                recyclerView.invalidate()
                if (messageList.isEmpty()) {
                    binding.noMessagesTextview.visibility = View.VISIBLE
                } else {
                    binding.noMessagesTextview.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    companion object {
        private const val TAG = "ChatActivity"
    }
}