package url.shortener;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        AppDependencies appDependencies = new AppDependencies(AppConfig.load());
        new Server(appDependencies.getUrlShortener()).start();
    }
}
