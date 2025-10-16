package notification;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.DriverManager;
import java.sql.SQLException;

class AppDependencies {

    private final NotificationController notificationController;

    AppDependencies(AppConfig config) throws SQLException {
        var connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        var context = DSL.using(connection, SQLDialect.POSTGRES);
        var repository = new NotificationSqlRepository(context);
        var producer = new NotificationProducer(new KafkaProducer<>(config.producerConfig()), config.getNotificationsTopic());
        var service = new NotificationService(repository, producer);
        notificationController = new NotificationController(service);
    }

    public NotificationController getNotificationController() {
        return notificationController;
    }
}
