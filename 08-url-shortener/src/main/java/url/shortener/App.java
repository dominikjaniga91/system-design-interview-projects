package url.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws SQLException {
        AppDependencies appDependencies = new AppDependencies(AppConfig.load());
        new Server(appDependencies.getUrlShortener()).start();
        LOGGER.info("Server started");
    }
}
