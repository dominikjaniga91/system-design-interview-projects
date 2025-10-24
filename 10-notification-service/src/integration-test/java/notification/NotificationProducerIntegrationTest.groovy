package notification

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.Duration

import static org.apache.kafka.clients.consumer.ConsumerConfig.*
import static org.apache.kafka.clients.producer.ProducerConfig.*

@Testcontainers
@Stepwise
class NotificationProducerIntegrationTest extends Specification {

    private static final String TOPIC = 'notifications'

    @Shared
    def kafka = new KafkaContainer('apache/kafka-native:4.1.0')

    def producer
    def consumer

    def setupSpec() {
        kafka.start()
        createTopic(TOPIC, 1, (short) 1)
    }

    def setup() {
        producer = new NotificationProducer(new KafkaProducer<String, Notification>(producerConfig()), TOPIC)
        consumer = new KafkaConsumer<String, Notification>(consumerConfig())
        consumer.subscribe([TOPIC])
    }

    def "should publish notification message to broker"() {
        given:
        def notification = new Notification(
                'user-service',
                [NotificationType.SMS, NotificationType.EMAIL],
                '23141',
                ['23145', '43523'],
                'Email verification',
                'Your email has been verified'
        )

        when:
        producer.send(1, notification)

        then:
        def records = consumer.poll(Duration.ofSeconds(5))
        def topicRecords = records.records(TOPIC)

        expect:
        !topicRecords.isEmpty()
        topicRecords.first().value().title() == 'Email verification'
    }

    private Map<String, Object> producerConfig() {
        [
                (BOOTSTRAP_SERVERS_CONFIG): kafka.bootstrapServers,
                (KEY_SERIALIZER_CLASS_CONFIG): StringSerializer,
                (VALUE_SERIALIZER_CLASS_CONFIG): NotificationSerializer,
                (ACKS_CONFIG): 'all',
                (RETRY_BACKOFF_MS_CONFIG): '1000',
                (DELIVERY_TIMEOUT_MS_CONFIG): '30000',
                (LINGER_MS_CONFIG): '0',
                (ENABLE_IDEMPOTENCE_CONFIG): true
        ] as Map<String, Object>
    }

    private Map<String, Object> consumerConfig() {
        [
                (BOOTSTRAP_SERVERS_CONFIG): kafka.bootstrapServers,
                (KEY_DESERIALIZER_CLASS_CONFIG): StringDeserializer,
                (VALUE_DESERIALIZER_CLASS_CONFIG): NotificationDeserializer,
                (RETRY_BACKOFF_MS_CONFIG): '1000',
                (AUTO_OFFSET_RESET_CONFIG): 'earliest',
                (GROUP_ID_CONFIG): 'notifications'
        ] as Map<String, Object>
    }

    private void createTopic(String topic, int partitions, short replicas) {
        AdminClient.create(producerConfig()).withCloseable { admin ->
            def newTopic = new NewTopic(topic, partitions, replicas)
            admin.createTopics([newTopic]).all().get()
        }
    }

    def cleanupSpec() {
        kafka.stop()
    }
}
