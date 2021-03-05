package com.example.socialapp.data

import com.example.socialapp.models.Bleep
import com.example.socialapp.models.User

object AppData {
    lateinit var userList: MutableList<User>
    lateinit var chatKeyList: MutableList<String>
    lateinit var bleepList: MutableList<Bleep>

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