package notification;

import data.tables.Notifications;
import groovy.util.logging.Slf4j;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
class NotificationSqlRepository implements NotificationRepository {

    private static final Logger log = LoggerFactory.getLogger(NotificationSqlRepository.class);
    private final DSLContext dslContext;

    NotificationSqlRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Integer saveNotification(Notification notification) {
        log.info("Saving notification: {}", notification);
        var record = dslContext.newRecord(Notifications.NOTIFICATIONS, notification);
        record.store();
        return record.getId();
    }
}
