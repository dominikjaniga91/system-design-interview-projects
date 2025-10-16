package notification;

import com.google.gson.Gson;

import static java.lang.String.format;
import static spark.Spark.post;

class NotificationController {

    private static final String BASE_PATH = "/api/v1";
    private final NotificationService notificationService;

    NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    void routes() {
        post(BASE_PATH + "/notification", (req, res) -> {
            var request = new Gson().fromJson(req.body(), Notification.class);
            Integer id = notificationService.saveNotification(request);
            return format("Notification received, id: %s", id);
        });
    }
}
