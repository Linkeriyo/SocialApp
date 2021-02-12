package com.example.socialapp.models

import java.text.SimpleDateFormat
import java.util.*

class Bleep(var user: User?, var timeMillis: Long?, var content: String?) {

    companion object {
        fun timeStringFromMillis(timeMillis: Long): String {
            var millisDiff = System.currentTimeMillis() - timeMillis
            if (millisDiff < 0) {
                millisDiff = 0
            }
            return if (millisDiff < 60000) {
                " • " + millisDiff / 1000 + "s"
            } else if (millisDiff < 3600000) {
                " • " + millisDiff / 60000 + "m"
            } else if (millisDiff < 86400000) {
                " • " + millisDiff / 3600000 + "h"
            } else {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                " • " + sdf.format(Date(timeMillis))
            }
        }
    }
}