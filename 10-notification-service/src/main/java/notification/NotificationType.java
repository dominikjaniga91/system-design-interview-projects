package notification;

import com.fasterxml.jackson.annotation.JsonCreator;

enum NotificationType {
    SMS, EMAIL;

    @JsonCreator
    NotificationType from(String value) {
        return NotificationType.valueOf(value);
    }
}
