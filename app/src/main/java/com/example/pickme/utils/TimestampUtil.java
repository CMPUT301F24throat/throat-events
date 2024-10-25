package com.example.pickme.utils;

import com.google.firebase.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimestampUtil {

    // Convert LocalDateTime to Firebase Timestamp
    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return new Timestamp(date);
    }

    // Convert Firebase Timestamp to LocalDateTime
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}