package com.zkAttendance.demo.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final DateTimeFormatter ZOHO_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static String now() {
        return LocalDateTime
                .now(ZoneId.systemDefault())
                .format(ZOHO_FORMAT);
    }
}
