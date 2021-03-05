package com.example.socialapp.data

import com.example.socialapp.models.Bleep
import com.example.socialapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.function.Consumer

object AppData {
    lateinit var userList: MutableList<User>
    lateinit var chatKeyList: MutableList<String>
    lateinit var bleepList: MutableList<Bleep>

    fun updateUserList() {
        val usersReference = FirebaseDatabase.getInstance().getReference("users")
        usersReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val users = mutableListOf<User>()
                task.result!!.children.forEach(Consumer {
                    child: DataSnapshot -> child.getValue(User::class.java)?.let { users.add(it) }
                })
                userList = users
            }
        }
    }

    fun getUserById(uid: String): User? {
        userList.forEach {
            if (uid == it.uid) {
                return it
            }
        }
        return null
    }

    fun getBleep(uid: String, timeMillis: Long): Bleep? {
        bleepList.forEach {
            if (uid == it.uid && timeMillis == it.timeMillis) {
                return it
            }
        }
        return null
    }
}