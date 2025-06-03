package url.shortener;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

@Testcontainers
class BaseContainerIntegrationTest {

    @Container
    final static PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.5"))
            .withDatabaseName("urls")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    final static GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>("redis:8.0.1").withExposedPorts(6379);

    static DSLContext context;
    static Connection connection;
    static JedisPool jedisPool;

    @BeforeAll
    static void init() throws SQLException {
        connection = DriverManager.getConnection(POSTGRES_CONTAINER.getJdbcUrl(), POSTGRES_CONTAINER.getUsername(), POSTGRES_CONTAINER.getPassword());
        context = DSL.using(connection, SQLDialect.POSTGRES);
        jedisPool = new JedisPool(REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(6379));
        prepareDatabase(context);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    static void prepareDatabase(DSLContext context) {
        context.execute("""
                CREATE SEQUENCE url_id_seq START 10_000_000;
                
                CREATE TABLE urls (
                          id INTEGER PRIMARY KEY DEFAULT nextval('url_id_seq'),
                          short_url VARCHAR DEFAULT NULL,
                          long_url  VARCHAR DEFAULT NULL UNIQUE
                      );
                """);
    }
}
