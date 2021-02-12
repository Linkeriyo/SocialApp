package com.example.socialapp.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.BleepsActivity
import com.example.socialapp.R
import com.example.socialapp.auth.LoginActivity.Companion.isUserRegistered
import com.example.socialapp.databinding.ActivityRegisterBinding
import com.example.socialapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mikhaellopez.circularimageview.CircularImageView

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isUserRegistered) {
            startActivity(Intent(this, BleepsActivity::class.java))
            finish()
        }
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    @SuppressLint("CheckResult")
    fun setup() {
        val registerEmailTextView = binding.registerEmailTextView
        val registerNameTextView = binding.registerNameTextView
        val registerNickTextView = binding.registerNickTextView
        val registerButton = binding.registerFinalButton
        val registerImageView = binding.registerImageView
        val user = FirebaseAuth.getInstance().currentUser
        registerEmailTextView.setText(user?.email)

        registerButton.setOnClickListener {
            if (!registerEmailTextView.text.equals("")
                    && registerNameTextView.text.equals("")
                    && registerNickTextView.text.equals("")) {
                val usersReference = FirebaseDatabase.getInstance().getReference("users")
                usersReference.child(user!!.uid).setValue(User(user.uid,
                        registerNameTextView.text.toString(),
                        registerNickTextView.text.toString(),
                        "a"
                ))
                nextActivity()
            } else {
                Toast.makeText(this,
                        "Todos los campos son obligatorios",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun nextActivity() {
        startActivity(Intent(this, BleepsActivity::class.java))
        finish()
    }
}