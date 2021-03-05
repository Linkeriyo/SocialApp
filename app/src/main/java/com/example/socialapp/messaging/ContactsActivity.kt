package com.example.socialapp.messaging

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.data.AppData
import com.example.socialapp.databinding.ActivityContactsBinding
import com.example.socialapp.messaging.ChatActivity
import com.example.socialapp.models.User
import com.example.socialapp.readapters.ContactsAdapter
import com.example.socialapp.readapters.ContactsAdapter.OnChatClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.function.Consumer

class ContactsActivity : AppCompatActivity(), OnChatClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityContactsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setup()
        loadData()
        setContentView(binding.root)
    }

    private fun loadData() {
        val usersReference = FirebaseDatabase.getInstance().getReference("users")
        usersReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                AppData.userList.clear()
                val users = mutableListOf<User>()
                task.result!!.children.forEach(Consumer { child: DataSnapshot -> child.getValue(User::class.java)?.let { users.add(it) } })
                AppData.userList.addAll(users)
                updateRecyclerView()
                binding.contactsSwiperefresh.isRefreshing = false
            }
        }
    }

    private fun setup() {
        recyclerView = binding.contactRecyclerView
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val mAdapter = ContactsAdapter(AppData.userList, this, this)
        recyclerView.adapter = mAdapter
        val toolbar = binding.toolbarContacts
        binding.contactsSwiperefresh.setOnRefreshListener {
            loadData()
        }
        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onChatClick(position: Int) {
        val otherUser = AppData.userList[position]
        Log.d(TAG, otherUser.userId!!)
        var chatKey: String? = null
        for (key in AppData.chatKeyList) {
            if (key.contains(otherUser.userId!!) && key.contains(FirebaseAuth.getInstance().uid!!)) {
                chatKey = key
            }
        }
        if (chatKey == null) {
            chatKey = FirebaseAuth.getInstance().uid + "-" + otherUser.userId
        }
        startActivity(Intent(this, ChatActivity::class.java)
                .putExtra("chatKey", chatKey)
                .putExtra("otherUserNick", otherUser.nick)
        )
    }

    fun updateRecyclerView() {
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.invalidate()
        val userListWoCurrentUser = AppData.userList.toMutableList()

        userListWoCurrentUser.forEach {
            if (FirebaseAuth.getInstance().uid.equals(it.userId)) {
                userListWoCurrentUser.remove(it)
            }
        }

        if (userListWoCurrentUser.isEmpty()) {
            binding.noContactsTextView.visibility = View.VISIBLE
        } else {
            binding.noContactsTextView.visibility = View.INVISIBLE
        }
    }

    companion object {
        private const val TAG = "ContactsActivity"
    }
}