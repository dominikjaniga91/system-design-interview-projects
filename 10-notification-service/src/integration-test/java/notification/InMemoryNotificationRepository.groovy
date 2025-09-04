package notification

public class InMemoryNotificationRepository implements NotificationRepository {

    @Override
    public Integer saveNotification(Notification notification) {
        return 1;
    }
}
