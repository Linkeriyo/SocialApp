package com.example.socialapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialapp.data.AppData
import com.example.socialapp.data.DataChecking
import com.example.socialapp.databinding.ActivityBleepDetailsBinding
import com.example.socialapp.models.Bleep
import com.example.socialapp.models.User
import com.example.socialapp.readapters.BleepsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import java.util.function.Consumer

class BleepDetailsActivity : AppCompatActivity(), BleepsAdapter.OnBleepClickListener {
    private lateinit var bleep: Bleep
    private lateinit var binding: ActivityBleepDetailsBinding
    private var commentsList = mutableListOf<Bleep>()
    private lateinit var user: User
    private var isComment = false
    private lateinit var commentPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getStringExtra("commentPath") != null) {
            isComment = true
            commentPath = intent.getStringExtra("commentPath")!!
        }
        if (isComment) {
            val bleepReference = FirebaseDatabase.getInstance().getReference(commentPath)
            bleepReference.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    bleep = it.result!!.getValue(Bleep::class.java)!!
                }
            }
        } else {
            bleep = AppData.bleepList[intent.getIntExtra("bleepPos", -1)]
        }
        user = AppData.getUserById(bleep.uid)!!
        binding = ActivityBleepDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
        loadComments()
    }

    private fun setup() {
        val recyclerView = binding.bleepCommentsRecyclerview
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BleepsAdapter(commentsList, this, this)
        Glide.with(this).load(user.image).into(binding.bleepDetailsPic)
        binding.bleepDetailsNick.text = user.nick
        binding.bleepDetailsTime.text = Bleep.timeStringFromMillis(bleep.timeMillis)
        binding.bleepDetailsContent.text = bleep.content
        binding.bleepDetailsToolbar.setNavigationOnClickListener { finish() }
        binding.bleepDetailsSweeprefresh.setOnRefreshListener {
            loadComments()
        }
        if (!isComment) {
            binding.isCommentTextview.visibility = View.GONE
        }
        binding.sendBleepCommentButton.setOnClickListener {
            val bleepContentView = binding.bleepCommentTextview
            var bleepOkay = true
            if (bleepContentView.text.toString().isEmpty()) {
                Toast.makeText(
                        this,
                        "No puedes publicar un Bleep vacío.",
                        Toast.LENGTH_SHORT
                ).show()
                bleepOkay = false
            }
            if (bleepContentView.text.length >= 240) {
                Toast.makeText(
                        this,
                        "Un Bleep no puede tener más de 240 caracteres.",
                        Toast.LENGTH_SHORT
                ).show()
                bleepOkay = false
            }
            if (bleepOkay) {
                val auth = FirebaseAuth.getInstance()
                val user = AppData.getUserById(auth.uid!!)!!
                val comment = Bleep(user.uid, System.currentTimeMillis(), bleepContentView.text.toString())
                bleep.comments.add(comment)
                val bleepsReference = FirebaseDatabase.getInstance().getReference("bleeps")
                if (DataChecking.isBleepOk(comment)) {
                    bleepsReference.child((Long.MAX_VALUE - bleep.timeMillis).toString() + "-" + bleep.uid).setValue(bleep)
                }
                bleepContentView.setText("")
            }
        }
    }

    private fun loadComments() {
        val bleepPath = (Long.MAX_VALUE - bleep.timeMillis).toString() + "-" + bleep.uid
        val commentsReference = FirebaseDatabase.getInstance()
                .getReference("bleeps")
                .child(bleepPath)
                .child("comments")
        commentsReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                commentsList.clear()
                val comments = mutableListOf<Bleep>()
                task.result!!.children.forEach(Consumer { child: DataSnapshot ->
                    child.getValue(Bleep::class.java)?.let { comments.add(it) }
                })
                commentsList.addAll(comments)
                updateRecyclerView()
                binding.bleepDetailsSweeprefresh.isRefreshing = false
            }
        }
    }

    private fun updateRecyclerView() {
        val recyclerView = binding.bleepCommentsRecyclerview
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.invalidate()
    }

    override fun onBleepClick(position: Int) {
    }
}