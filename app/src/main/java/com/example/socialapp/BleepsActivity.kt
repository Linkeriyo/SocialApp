package com.example.socialapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.auth.EditProfileActivity
import com.example.socialapp.auth.LoginActivity
import com.example.socialapp.data.AppData
import com.example.socialapp.databinding.ActivityBleepsBinding
import com.example.socialapp.messaging.ContactsActivity
import com.example.socialapp.models.Bleep
import com.example.socialapp.readapters.BleepsAdapter
import com.example.socialapp.readapters.BleepsAdapter.OnBleepClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import java.util.function.Consumer

class BleepsActivity : AppCompatActivity(), OnBleepClickListener {


    private lateinit var binding: ActivityBleepsBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bleeps_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.dm_option -> {
                directMessageOptionPress()
                return true
            }
            R.id.sign_out_option -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            R.id.edit_profile_option -> {
                startActivityForResult(Intent(this, EditProfileActivity::class.java), PROFILE_EDITED)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBleepsBinding.inflate(layoutInflater)
        loadBleeps()
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        recyclerView = binding.bleepsRecyclerView
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val mAdapter = BleepsAdapter(AppData.bleepList, this, this)
        recyclerView.adapter = mAdapter
        if (AppData.bleepList.isNotEmpty()) {
            binding.noBleepsTextView.visibility = View.INVISIBLE
        }
        val newBleepButton = binding.newBleepButton
        newBleepButton.setOnClickListener { startActivityForResult(Intent(this, NewBleepActivity::class.java), NEW_BLEEP) }
        setSupportActionBar(binding.bleepsToolbar)
        binding.bleepsSwiperefresh.setOnRefreshListener {
            loadBleeps()
        }
    }

    private fun loadBleeps() {
        val bleepsReference = FirebaseDatabase.getInstance().getReference("bleeps")
        bleepsReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                AppData.bleepList.clear()
                val bleeps = ArrayList<Bleep>()
                task.result!!.children.forEach(Consumer { child: DataSnapshot ->
                    child.getValue(Bleep::class.java)?.let { bleeps.add(it) }
                })
                AppData.bleepList.addAll(bleeps)
                updateRecyclerView()
                binding.bleepsSwiperefresh.isRefreshing = false
            }
        }
    }

    private fun updateRecyclerView() {
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.invalidate()
        if (AppData.bleepList.isNotEmpty()) {
            binding.noBleepsTextView.visibility = View.INVISIBLE
        } else {
            binding.noBleepsTextView.visibility = View.VISIBLE
        }
    }

    private fun directMessageOptionPress() {
        startActivity(Intent(this, ContactsActivity::class.java))
    }

    override fun onBleepClick(position: Int) {
        startActivity(Intent(this, BleepDetailsActivity::class.java).putExtra("bleepPos", position))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_BLEEP || requestCode == PROFILE_EDITED) {
            loadBleeps()
        }
    }

    companion object {
        private const val TAG = "BleepsActivity"
        private const val NEW_BLEEP = 1
        private const val PROFILE_EDITED = 2
    }
}