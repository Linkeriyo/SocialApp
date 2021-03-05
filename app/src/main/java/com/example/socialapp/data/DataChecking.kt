package com.example.socialapp.data

import com.example.socialapp.models.Bleep
import com.example.socialapp.models.User

object DataChecking {
    fun isUserOk(user: User?): Boolean {
        if (user == null) {
            return false;
        }
        return (user.image != ""
                && user.name != ""
                && user.nick != ""
                && user.uid != "")
    }

    fun isBleepOk(bleep: Bleep?): Boolean {
        if (bleep == null) {
            return false
        }
        val user = AppData.getUserById(bleep.uid)
        return (bleep.content != ""
                && bleep.content.length < 240
                && user != null)
    }
}