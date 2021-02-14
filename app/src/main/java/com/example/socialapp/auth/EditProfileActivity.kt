package com.example.socialapp.auth

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.socialapp.data.AppData
import com.example.socialapp.databinding.ActivityEditProfileBinding
import com.example.socialapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var image : String
    private var auth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance()
    private var storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        val firebaseUser = auth.currentUser
        val databaseUser = firebaseUser?.let { AppData.getUser(it.uid) }
        database.getReference("users/${auth.currentUser!!.uid}").get().addOnCompleteListener {
            if (it.isSuccessful) {
                progressBar()
                image = it.result!!.child("image").getValue(String::class.java)!!
                putImage(image)
            }
        }
        binding.editProfileEmailTextView.setText(firebaseUser?.email)
        binding.editProfileNameTextView.setText(databaseUser?.name)
        binding.editProfileNickTextView.setText(databaseUser?.nick)
    }

    private fun progressBar() {
        val progressBar = binding.editProgressBar
        progressBar.progress = 200
        progressBar.visibility = View.VISIBLE
        progressBar.max = 1000
        ObjectAnimator.ofInt(progressBar, "progress", 999)
                .setDuration(2000)
                .start()
    }

    private fun putImage(uri: String) {
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.editProfileImageView)

        binding.editProgressBar.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RegisterActivity.PICK_IMAGE) {
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

    fun editPicPress(view: View) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, RegisterActivity.PICK_IMAGE)
    }

    fun confirmButtonPress(view: View) {
        if (binding.editProfileEmailTextView.text.isNotEmpty()
                && binding.editProfileNameTextView.text.isNotEmpty()
                && binding.editProfileNickTextView.text.isNotEmpty()
                && image.isNotEmpty()) {
            val user = auth.currentUser
            val usersReference = FirebaseDatabase.getInstance().getReference("users")
            usersReference.child(user!!.uid).setValue(User(user.uid,
                    binding.editProfileNameTextView.text.toString(),
                    binding.editProfileNickTextView.text.toString(),
                    image
            ))
            finish()
        } else {
            Toast.makeText(this,
                    "Todos los campos son obligatorios",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun cancelPress(view: View) {
        finish()
    }
}