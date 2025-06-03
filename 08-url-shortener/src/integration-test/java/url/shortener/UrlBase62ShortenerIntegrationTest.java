package url.shortener;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class UrlBase62ShortenerIntegrationTest extends BaseContainerIntegrationTest {

    private static UrlShortener shortener;

    @BeforeAll
    static void setUp() {
        shortener = new UrlBase62Shortener(
                new UrlSqlRepositoryImpl(
                        context,
                        new JedisPool(REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(6379))
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
}
