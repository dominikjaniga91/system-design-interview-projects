package notification;

import com.google.gson.Gson;

import static spark.Spark.post;

public class NotificationController {

    void routes() {
        post("/api/v1/notification", (req, res) -> {
            var request = new Gson().fromJson(req.body(), NotificationRequest.class);

            return "Notification received";
        });
    }
}
