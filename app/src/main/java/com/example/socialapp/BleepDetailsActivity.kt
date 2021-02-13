package com.example.socialapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.socialapp.data.AppData
import com.example.socialapp.databinding.ActivityBleepDetailsBinding
import com.example.socialapp.models.Bleep

class BleepDetailsActivity : AppCompatActivity() {
    private lateinit var bleep: Bleep
    private lateinit var binding: ActivityBleepDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBleepDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        bleep = AppData.bleepList[intent.getIntExtra("bleepPos", -1)]
        val bleepDetailsNick = binding.bleepDetailsNick
        val bleepDetailsTime = binding.bleepDetailsTime
        val bleepDetailsContent = binding.bleepDetailsContent
        val bleepDetailsPic = binding.bleepDetailsPic
        Glide.with(this).load(bleep.user.image).into(bleepDetailsPic)
        bleepDetailsNick.text = bleep.user.nick
        bleepDetailsTime.text = Bleep.timeStringFromMillis(bleep.timeMillis)
        bleepDetailsContent.text = bleep.content

        val toolbar = binding.bleepDetailsToolbar
        toolbar.setNavigationOnClickListener { finish() }
    }
}