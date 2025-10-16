package notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;

    NotificationService(NotificationRepository notificationRepository,
                        NotificationProducer notificationProducer) {
        this.notificationRepository = notificationRepository;
        this.notificationProducer = notificationProducer;
    }

    Integer saveNotification(Notification notification) {
        Integer id = notificationRepository.saveNotification(notification);
        notificationProducer.send(id, notification);
        return id;
    }
}
