package url.shortener;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPool;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class UrlBase62ShortenerIntegrationTest {

    private UrlShortener shortener;
    private PostgreSQLContainer<?> postgres;
    private GenericContainer<?> redis;

    @BeforeEach
    void setUp() throws SQLException {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("urls")
                .withUsername("testuser")
                .withPassword("testpass");
        redis = new GenericContainer<>("redis:8.0.1").withExposedPorts(6379);
        postgres.start();
        redis.start();

        var connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        var context = DSL.using(connection, SQLDialect.POSTGRES);
        prepareDatabase(context);
        shortener = new UrlBase62Shortener(
                new UrlSqlRepositoryImpl(
                        context,
                        new JedisPool(redis.getHost(), redis.getMappedPort(6379))
                ), new Base62Encoder()
        );
    }

    @Test
    void shouldPersistAndReturnShortenUrl_whenShorteningLongUrl() {
        //given
        String longUrl = "https://medium.com/@stefanovskyi/unit-test-naming-conventions-dd9208eadbea";

        //when
        String shorten = shortener.shorten(longUrl);

        //then
        assertThat(shorten).isEqualTo("KSxf");
    }

    @AfterEach
    void tearDown() {
        postgres.close();
        redis.close();
    }

    private void prepareDatabase(DSLContext context) {
        context.execute("""
                CREATE SEQUENCE custom_user_id_seq START 10_000_000;
                
                CREATE TABLE urls (
                          id INTEGER PRIMARY KEY DEFAULT nextval('custom_user_id_seq'),
                          short_url VARCHAR DEFAULT NULL,
                          long_url  VARCHAR DEFAULT NULL UNIQUE
                      );
                """);
    }
}
