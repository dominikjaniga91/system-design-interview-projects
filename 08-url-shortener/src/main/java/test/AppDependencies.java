package test;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppDependencies {

    private final BaseEncoder baseEncoder;
    private final URLShortener urlShortener;
    private final URLRepository repository;

    public AppDependencies(AppConfig config) throws SQLException {
        this.baseEncoder = new Base62Encoder();
        this.urlShortener = new URLShortener();
        var connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        var context = DSL.using(connection, SQLDialect.POSTGRES);
        this.repository = new URLRepositoryImpl(context);
    }
}
