package notification

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.sql.Connection
import java.sql.DriverManager

@Stepwise
@Testcontainers
class NotificationSqlRepositoryIntegrationSpec extends Specification {

    @Shared
    PostgreSQLContainer<PostgreSQLContainer> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.5"))
            .withDatabaseName("notifications")
            .withUsername("testuser")
            .withPassword("testpass")

    @Shared
    Connection connection

    @Shared
    DSLContext context

    @Shared
    NotificationSqlRepository notificationRepository

    def setupSpec() {
        connection = DriverManager.getConnection(
                POSTGRES.jdbcUrl,
                POSTGRES.username,
                POSTGRES.password
        )
        context = DSL.using(connection, SQLDialect.POSTGRES)
        notificationRepository = new NotificationSqlRepository(context)
        prepareDatabase()
    }

    def "should save notification in database"() {
        given:
        def notification = this.notification()

        when:
        def id = notificationRepository.saveNotification(notification)

        then:
        id != null
        id > 0
    }

    def notification() {
        return new Notification("user-service", List.of(NotificationType.SMS, NotificationType.EMAIL),
                "23141", List.of("23145", "43523"), "Email verification", "Your email has been verified");
    }

    void prepareDatabase() {
        context.execute("""
                CREATE SEQUENCE notification_id_seq START 1;
                CREATE TABLE notifications (
                                               id INTEGER PRIMARY KEY DEFAULT nextval('notification_id_seq'),
                                               service_name  VARCHAR NOT NULL,
                                               types TEXT[] NOT NULL,
                                               sender VARCHAR NOT NULL,
                                               recipients_ids TEXT[] NOT NULL,
                                               title TEXT NOT NULL,
                                               message VARCHAR NOT NULL
                );
            """)
    }
}