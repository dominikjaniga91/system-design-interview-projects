package notification;

import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class NotificationSerializer implements Serializer<Notification> {

    @Override
    public byte[] serialize(String s, Notification notification) {
        return notification == null ? null : notification.toJson().getBytes(StandardCharsets.UTF_8);
    }
}
