package com.example.socialapp.models

import java.text.SimpleDateFormat
import java.util.*

class Bleep {

    var uid = ""
    var timeMillis: Long = 0
    var content = ""
    var comments = mutableListOf<Bleep>()

    constructor() {
    }

    constructor(uid: String, timeMillis: Long, content: String) {
        this.uid = uid
        this.timeMillis = timeMillis
        this.content = content
    }

    companion object {
        fun timeStringFromMillis(timeMillis: Long): String {
            var millisDiff = System.currentTimeMillis() - timeMillis
            if (millisDiff < 0) {
                millisDiff = 0
            }
            return when {
                millisDiff < 60000 -> {
                    " • " + millisDiff / 1000 + "s"
                }
                millisDiff < 3600000 -> {
                    " • " + millisDiff / 60000 + "m"
                }
                millisDiff < 86400000 -> {
                    " • " + millisDiff / 3600000 + "h"
                }
                else -> {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    " • " + sdf.format(Date(timeMillis))
                }
            }
        }
    }
}