package com.tornado.chatbot.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {
    public String readableDate(long timestamp) {  // Note: Method name matches record convention
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
    }
}
