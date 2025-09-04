package notification;

import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.DriverManager;
import java.sql.SQLException;

class AppDependencies {

    private final NotificationController notificationController;

    AppDependencies(AppConfig config) throws SQLException {
        var connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        var context = DSL.using(connection, SQLDialect.POSTGRES);
        var notificationRepository = new NotificationSqlRepository(context);
        notificationController = new NotificationController(notificationRepository);
    }

    public NotificationController getNotificationController() {
        return notificationController;
    }
}
