package url.shortener;

import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import redis.clients.jedis.JedisPool;

import java.sql.DriverManager;
import java.sql.SQLException;

class AppDependencies {

    private final UrlController urlController;

    AppDependencies(AppConfig config) throws SQLException {
        var connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        var context = DSL.using(connection, SQLDialect.POSTGRES);
        var jedisPool = new JedisPool(config.getJedisHost(), config.getJedisPort());
        UrlRepository urlRepository = new UrlSqlRepositoryImpl(context, jedisPool);
        urlController = new UrlController(new UrlBase62Shortener(urlRepository, new Base62Encoder()), urlRepository);
    }

    UrlController getUrlController() {
        return urlController;
    }
}
