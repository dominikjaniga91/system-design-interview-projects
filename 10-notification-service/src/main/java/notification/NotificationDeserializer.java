package notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class NotificationDeserializer implements Deserializer<Notification> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Notification deserialize(String topic, byte[] data) {
        try {
            return mapper.readValue(data, Notification.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing Notification", e);
        }
    }
}