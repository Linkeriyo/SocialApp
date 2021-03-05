package com.example.socialapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialapp.data.AppData
import com.example.socialapp.databinding.ActivityBleepDetailsBinding
import com.example.socialapp.models.Bleep
import com.example.socialapp.readapters.BleepsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import java.util.function.Consumer

class BleepDetailsActivity : AppCompatActivity(), BleepsAdapter.OnBleepClickListener {
    private lateinit var bleep: Bleep
    private lateinit var binding: ActivityBleepDetailsBinding
    private var commentsList = mutableListOf<Bleep>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bleep = AppData.bleepList[intent.getIntExtra("bleepPos", -1)]
        binding = ActivityBleepDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        val recyclerView = binding.bleepCommentsRecyclerview
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BleepsAdapter(commentsList, this, this)
        Glide.with(this).load(bleep.user?.image).into(binding.bleepDetailsPic)
        binding.bleepDetailsNick.text = bleep.user?.nick
        binding.bleepDetailsTime.text = Bleep.timeStringFromMillis(bleep.timeMillis)
        binding.bleepDetailsContent.text = bleep.content
        binding.bleepDetailsToolbar.setNavigationOnClickListener { finish() }
        binding.bleepDetailsSweeprefresh.setOnRefreshListener {
            loadComments()
        }
    }

    private fun loadComments() {
        val bleepPath = (Long.MAX_VALUE - bleep.timeMillis).toString() + "-" + bleep.user?.userId
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
        TODO("Not yet implemented")
    }
}