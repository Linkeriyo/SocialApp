package com.example.socialapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {

    public String sender;
    public Long timeMillis;
    public String content;

    public Message(String key, String value) {
        sender = key.split("-")[1];
        timeMillis = Long.valueOf(key.split("-")[0]);
        content = value;
    }

    public static String timeStringFromMillis(long timeMillis) {
        long millisDiff = System.currentTimeMillis() - timeMillis;
        if (millisDiff < 0) {
            millisDiff = 0;
        }
        if (millisDiff < 60000) {
            return millisDiff/1000 + "s";
        } else if (millisDiff < 3600000) {
            return millisDiff/60000  + "m";
        } else if (millisDiff < 86400000) {
            return millisDiff/3600000 + "h";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            return sdf.format(new Date(timeMillis));
        }
    }
}
