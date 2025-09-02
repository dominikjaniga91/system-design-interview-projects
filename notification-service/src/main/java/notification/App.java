package notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws SQLException {
        AppConfig config = AppConfig.load();
        AppDependencies dependencies = new AppDependencies(config);
        new Server(config, dependencies.getNotificationController()).start();
        LOGGER.info("Server started");
    }
}
