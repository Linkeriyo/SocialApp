package com.example.socialapp.data

import com.example.socialapp.models.Bleep
import com.example.socialapp.models.User

object DataChecking {
    fun isUserOk(user: User?): Boolean {
        if (user == null) {
            return false;
        }
        return (user.image != null
                && user.name != null
                && user.nick != null
                && user.userId != null)
    }

    fun isBleepOk(bleep: Bleep?): Boolean {
        if (bleep == null) {
            return false
        }
        return (bleep.content != null
                && bleep.content!!.length < 240
                && bleep.user != null)
    }
}