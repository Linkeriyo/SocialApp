package com.example.socialapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.auth.LoginActivity
import com.example.socialapp.data.AppData
import com.example.socialapp.models.Bleep
import com.example.socialapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import java.util.function.Consumer

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
    }

    private val context = this
    private var bleepsLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val usersReference = FirebaseDatabase.getInstance().getReference("users")
        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                snapshot.children.forEach(Consumer { child: DataSnapshot -> users.add(child.value as User) })
                AppData.userList = users
                Thread(NextActivityWaiter(this)).start()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }

            inner class NextActivityWaiter(val eventListener: ValueEventListener) : Runnable {
                override fun run() {
                    while (!bleepsLoaded) {
                        try {
                            Thread.sleep(200)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                    startActivity(Intent(context, LoginActivity::class.java))
                    usersReference.removeEventListener(eventListener)
                    finish()
                }
            }
        })
        val chatsReference = FirebaseDatabase.getInstance().getReference("chats")
        chatsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatKeys = mutableListOf<String>()
                snapshot.children.forEach(Consumer { child: DataSnapshot -> chatKeys.add(child.key!!) })
                AppData.chatKeyList = chatKeys
                chatsReference.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        val bleepsReference = FirebaseDatabase.getInstance().getReference("bleeps")
        bleepsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bleeps = mutableListOf<Bleep>()
                snapshot.children.forEach(Consumer { child: DataSnapshot -> bleeps.add(child.value as Bleep) })
                AppData.bleepList = bleeps
                bleepsLoaded = true
                bleepsReference.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    //      __   (soy el guardian de bleeper)
    //  ___( o)> (bleep)
    //  \ <_. )
    //   `---'
}