package com.example.socialapp

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.data.AppData
import com.example.socialapp.data.DataChecking
import com.example.socialapp.databinding.ActivityNewBleepBinding
import com.example.socialapp.models.Bleep
import com.example.socialapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class NewBleepActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewBleepBinding

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.new_bleep_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.send_bleep_option) {
            newBleepPress()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBleepBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        val toolbar = binding.newBleepToolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        val bleepContentView = binding.editTextBleepContent
        bleepContentView.setOnEditorActionListener { _: TextView?, _: Int, _: KeyEvent? ->
            toolbar.findViewById<View>(R.id.send_bleep_option).isEnabled = bleepContentView.text.toString().isNotEmpty()
            false
        }
    }

    private fun newBleepPress() {
        val bleepContentView = binding.editTextBleepContent
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
            val timeMillis = System.currentTimeMillis()
            val auth = FirebaseAuth.getInstance()
            val user = currentUserFromList!!
            val bleep = Bleep(user.uid, timeMillis, bleepContentView.text.toString())
            val bleepsReference = FirebaseDatabase.getInstance().getReference("bleeps")
            if (DataChecking.isBleepOk(bleep)) {
                bleepsReference.child((Long.MAX_VALUE - timeMillis).toString() + "-" + auth.uid).setValue(bleep)
            }
            finish()
        }
    }

    private val currentUserFromList: User?
        get() {
            for (user in AppData.userList) {
                if (user.uid == FirebaseAuth.getInstance().uid) {
                    return user
                }
            }
            return null
        }
}