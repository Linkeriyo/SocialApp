package com.example.socialapp.models

import java.text.SimpleDateFormat
import java.util.*

class Message(key: String, value: String) {
    var sender: String = key.split("-").toTypedArray()[1]
    var timeMillis: Long = java.lang.Long.valueOf(key.split("-").toTypedArray()[0])
    var content: String = value

    companion object {
        fun timeStringFromMillis(timeMillis: Long): String {
            var millisDiff = System.currentTimeMillis() - timeMillis
            if (millisDiff < 0) {
                millisDiff = 0
            }
            return if (millisDiff < 60000) {
                (millisDiff / 1000).toString() + "s"
            } else if (millisDiff < 3600000) {
                (millisDiff / 60000).toString() + "m"
            } else if (millisDiff < 86400000) {
                (millisDiff / 3600000).toString() + "h"
            } else {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                sdf.format(Date(timeMillis))
            }
        }
    }

}