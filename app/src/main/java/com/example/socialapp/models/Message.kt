package com.example.socialapp.models

import java.text.SimpleDateFormat
import java.util.*

class Message {

    constructor() {

    }

    constructor(key: String, value: String) {
        this.sendersUid = key.split("-").toTypedArray()[1]
        this.timeMillis = java.lang.Long.valueOf(key.split("-").toTypedArray()[0])
        this.content = value
    }

    var sendersUid: String? = null
    var timeMillis: Long = 0
    var content: String? = null

    companion object {
        fun timeStringFromMillis(timeMillis: Long): String {
            var millisDiff = System.currentTimeMillis() - timeMillis
            if (millisDiff < 0) {
                millisDiff = 0
            }
            return when {
                millisDiff < 60000 -> {
                    (millisDiff / 1000).toString() + "s"
                }
                millisDiff < 3600000 -> {
                    (millisDiff / 60000).toString() + "m"
                }
                millisDiff < 86400000 -> {
                    (millisDiff / 3600000).toString() + "h"
                }
                else -> {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    sdf.format(Date(timeMillis))
                }
            }
        }
    }

}