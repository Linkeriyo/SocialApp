package com.example.socialapp.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.socialapp.BleepsActivity
import com.example.socialapp.auth.LoginActivity.Companion.isUserRegistered
import com.example.socialapp.databinding.ActivityRegisterBinding
import com.example.socialapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.example.socialapp.data.DataChecking

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RegsiterActivity"
        const val PICK_IMAGE = 1
    }

    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var image: String

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

    private fun setup() {
        database.getReference("defaultuserimg").get().addOnCompleteListener {
            if (it.isSuccessful) {
                image = it.result!!.getValue(String::class.java)!!
            }
        }
        val user = auth.currentUser
        database.getReference("users/${auth.currentUser!!.uid}").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result!!.child("image").getValue(String::class.java) != null) {
                    progressBar()
                    image = task.result!!.child("image").getValue(String::class.java)!!
                    putImage(image)
                }
            }
        }
        binding.registerEmailTextView.setText(user?.email)
    }

    private fun nextActivity() {
        startActivity(Intent(this, BleepsActivity::class.java))
        finish()
    }

    fun registerButtonPress(view: View) {
        if (!binding.registerEmailTextView.text.equals("")
                && !binding.registerNameTextView.text.equals("")
                && !binding.registerNickTextView.text.equals("")) {
            val firebaseUser = auth.currentUser
            val usersReference = database.getReference("users")
            val user = User(firebaseUser!!.uid,
                    binding.registerNameTextView.text.toString(),
                    binding.registerNickTextView.text.toString(),
                    image
            )
            if (DataChecking.isUserOk(user)) {
                usersReference.child(firebaseUser.uid).setValue(user)
                nextActivity()
            } else {
                Toast.makeText(this,
                        "Ha ocurrido un error",
                        Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            Toast.makeText(this,
                    "Todos los campos son obligatorios",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun editPicPress(view: View) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            data?.let {
                progressBar()

                val imageUri = data.data
                val refFilePath = storage
                        .getReference("images").child(imageUri!!.lastPathSegment!!)

                refFilePath.putFile(imageUri).continueWithTask {
                    if (!it.isSuccessful) {
                        throw it.exception!!
                    }
                    refFilePath.downloadUrl
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        image = it.result.toString()
                        database.getReference("users").child(auth.uid!!).child("image")
                                .setValue(image)
                        putImage(image)

                        Toast.makeText(this,
                                "Imagen actualizada",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun putImage(uri: String) {
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.registerImageView)

        binding.registerProgressBar.visibility = View.GONE
    }

    private fun progressBar() {
        val progressBar = binding.registerProgressBar
        progressBar.progress = 200
        progressBar.visibility = View.VISIBLE
        progressBar.max = 1000
        ObjectAnimator.ofInt(progressBar, "progress", 999)
                .setDuration(2000)
                .start()
    }
}