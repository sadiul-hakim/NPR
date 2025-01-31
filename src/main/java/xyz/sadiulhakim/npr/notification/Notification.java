package xyz.sadiulhakim.npr.notification;

public record Notification(
        String title,
        String message,
        String dateTime,
        boolean seen
) {
}
