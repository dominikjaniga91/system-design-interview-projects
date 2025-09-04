package notification;

import com.google.gson.Gson;

import static java.lang.String.format;
import static spark.Spark.post;

class NotificationController {

    private static final String BASE_PATH = "/api/v1";
    private final NotificationRepository notificationRepository;

    NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    void routes() {
        post(BASE_PATH + "/notification", (req, res) -> {
            var request = new Gson().fromJson(req.body(), Notification.class);
            Integer id = notificationRepository.saveNotification(request);
            return format("Notification received, id: %s", id);
        });
    }
}
