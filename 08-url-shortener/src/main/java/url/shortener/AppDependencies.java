package url.shortener;

import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.DriverManager;
import java.sql.SQLException;

class AppDependencies {

    private final UrlShortener urlShortener;

    AppDependencies(AppConfig config) throws SQLException {
        var connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        var context = DSL.using(connection, SQLDialect.POSTGRES);
        urlShortener = new UrlBase62Shortener(new UrlSqlRepositoryImpl(context), new Base62Encoder());
    }

    public UrlShortener getUrlShortener() {
        return urlShortener;
    }
}
