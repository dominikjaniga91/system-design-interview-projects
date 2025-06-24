package gateway;

import okhttp3.OkHttpClient;

import java.time.Duration;

public class App {

    public static void main(String[] args) {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
        AppConfig load = AppConfig.load();
        new Server(new UrlShortener(load.getUrlsShortenerUri(), okClient)).start();
    }
}
