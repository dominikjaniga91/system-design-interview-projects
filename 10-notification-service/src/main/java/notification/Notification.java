package notification;

import java.util.List;
import java.util.stream.Collectors;

record Notification(
        String serviceName,
        List<NotificationType> types,
        String sender,
        List<String> recipientsIds,
        String title,
        String message) {

    /*
    {
    "serviceName": user-service,
    "types": [SMS, EMAIL],
    "sender": 23141,
    "recipientsIds": [23145, 43523],
    "title": Email verification,
    "message": Your email has been verified
}
     */

    List<String> getTypesAsString() {
        return types.stream()
                .map(n -> String.format("\"%s\"", n))
                .collect(Collectors.toList());
    }

    String toJson() {
        return String.format("""
                {
                    "serviceName": "%s",
                    "types": %s,
                    "sender": "%s",
                    "recipientsIds": %s,
                    "title": "%s",
                    "message": "%s"
                }
                """,
                serviceName,
                getTypesAsString(),
                sender,
                recipientsIds,
                title,
                message
        );
    }
}
