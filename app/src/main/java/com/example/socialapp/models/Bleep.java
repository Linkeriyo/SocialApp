package com.example.socialapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Bleep {

    User user;
    Long timeMillis;
    String content;

    public Bleep() {

    }

    public Bleep(User user, Long timeMillis, String content) {
        this.user = user;
        this.timeMillis = timeMillis;
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public Long getTimeMillis() {
        return timeMillis;
    }

    public String getContent() {
        return content;
    }

    public static String timeStringFromMillis(long timeMillis) {
        long millisDiff = System.currentTimeMillis() - timeMillis;
        if (millisDiff < 0) {
            millisDiff = 0;
        }
        if (millisDiff < 60000) {
            return " • " + millisDiff/1000 + "s";
        } else if (millisDiff < 3600000) {
            return " • " + millisDiff/60000  + "m";
        } else if (millisDiff < 86400000) {
            return " • " + millisDiff/3600000 + "h";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            return " • " + sdf.format(new Date(timeMillis));
        }
    }
}
