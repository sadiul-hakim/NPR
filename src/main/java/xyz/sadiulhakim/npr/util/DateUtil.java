package xyz.sadiulhakim.npr.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private DateUtil() {
    }

    private static final DateTimeFormatter DEFAULT_FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDateTime dateTime) {
        return DEFAULT_FORMATER.format(dateTime);
    }

    public static String format(LocalDateTime dateTime, String format) {
        DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern(format);
        return FORMATER.format(dateTime);
    }
}
