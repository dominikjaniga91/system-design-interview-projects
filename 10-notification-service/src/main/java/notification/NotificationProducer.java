package notification;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaProducer<String, Notification> producer;
    private final String topic;

    NotificationProducer(KafkaProducer<String, Notification> producer, String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    public void send(int id, Notification notification) {
        producer.send(new ProducerRecord<>(topic, notification),
                (metadata, exception) -> {
                    if (exception != null) {
                         log.error("Failed to publish notification to topic {}", topic, exception);
                    } else {
                         log.info("Notification with id {} published to topic={} partition={} offset={}",
                                   id, metadata.topic(), metadata.partition(), metadata.offset());
                    }
                });
    }
}
