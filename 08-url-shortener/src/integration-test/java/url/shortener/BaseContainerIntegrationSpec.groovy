package url.shortener

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import redis.clients.jedis.JedisPool
import spock.lang.Shared
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager

@Testcontainers
class BaseContainerIntegrationSpec extends Specification {

    @Shared
    PostgreSQLContainer<PostgreSQLContainer> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.5"))
            .withDatabaseName("urls")
            .withUsername("testuser")
            .withPassword("testpass")

    @Shared
    GenericContainer<GenericContainer> REDIS = new GenericContainer<>("redis:8.0.1")
            .withExposedPorts(6379)

    @Shared
    DSLContext context

    @Shared
    Connection connection

    @Shared
    JedisPool jedisPool

    def setupSpec() {
        connection = DriverManager.getConnection(
                POSTGRES.jdbcUrl,
                POSTGRES.username,
                POSTGRES.password
        )
        context = DSL.using(connection, SQLDialect.POSTGRES)
        jedisPool = new JedisPool(REDIS.host, REDIS.getMappedPort(6379))
        prepareDatabase()
    }

    def cleanupSpec() {
        connection?.close()
    }

    void prepareDatabase() {
        context.execute("""
                CREATE SEQUENCE url_id_seq START 10000000;

                CREATE TABLE urls (
                      id INTEGER PRIMARY KEY DEFAULT nextval('url_id_seq'),
                      short_url VARCHAR DEFAULT NULL,
                      long_url  VARCHAR DEFAULT NULL UNIQUE
                );
            """)
    }
}