package url.shortener;

import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import redis.clients.jedis.JedisPool;

import java.sql.DriverManager;
import java.sql.SQLException;

class AppDependencies {

    private final UrlShortener urlShortener;
    private final UrlRepository urlRepository;

    AppDependencies(AppConfig config) throws SQLException {
        var connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        var context = DSL.using(connection, SQLDialect.POSTGRES);
        var jedisPool = new JedisPool(config.getJedisHost(), config.getJedisPort());
        urlRepository = new UrlSqlRepositoryImpl(context, jedisPool);
        urlShortener = new UrlBase62Shortener(urlRepository, new Base62Encoder());
    }

    public UrlShortener getUrlShortener() {
        return urlShortener;
    }

    public UrlRepository getUrlRepository() {
        return urlRepository;
    }
}
