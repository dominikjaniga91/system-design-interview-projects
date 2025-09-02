package notification;

import java.util.List;

public record NotificationRequest(
        String serviceName,
        List<NotificationType> types,
        String sender,
        List<String> recipientsIds,
        String title,
        String message) {

}
