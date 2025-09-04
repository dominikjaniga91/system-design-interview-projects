package notification;

import java.util.List;

record Notification(
        String serviceName,
        List<NotificationType> types,
        String sender,
        List<String> recipientsIds,
        String title,
        String message) {
}
