package notification;

import com.typesafe.config.Config;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

import static com.typesafe.config.ConfigFactory.defaultOverrides;
import static com.typesafe.config.ConfigFactory.parseResourcesAnySyntax;

class AppConfig {

    private final Config config;

    private AppConfig(Config config) {
        this.config = config;
    }

    public static AppConfig load() {
        String configFileName = String.format("%s.conf", System.getProperty("profile"));
        Config config = defaultOverrides()
                .withFallback(parseResourcesAnySyntax(configFileName))
                .resolve();

        return new AppConfig(config);
    }

    String getUsername() {
        return config.getString("database.username");
    }

    String getPassword() {
        return config.getString("database.password");
    }

    String getUrl() {
        return config.getString("database.url");
    }

    int serverPort() {
        return config.getInt("server.port");
    }

    Map<String, Object> producerConfig() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("broker.bootstrap-servers"));
        configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NotificationSerializer.class);
        configMap.put(ProducerConfig.ACKS_CONFIG, config.getString("broker.acks"));
        configMap.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, config.getString("broker.retry-backoff-ms"));
        configMap.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, config.getString("broker.delivery-timeout-ms"));
        configMap.put(ProducerConfig.LINGER_MS_CONFIG, config.getString("broker.linger-ms"));
        configMap.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return configMap;
    }

    String getNotificationsTopic() {
        return config.getString("broker.notifications-topic");
    }
}
