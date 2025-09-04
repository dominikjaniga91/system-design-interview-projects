package notification;

import static spark.Spark.port;

class Server {

    private final AppConfig appConfig;
    private final NotificationController notificationController;

    Server(AppConfig appConfig, NotificationController notificationController) {
        this.appConfig = appConfig;
        this.notificationController = notificationController;
    }

    public void start() {
        port(appConfig.serverPort());
        notificationController.routes();
    }
}