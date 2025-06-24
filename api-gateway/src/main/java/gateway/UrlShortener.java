package gateway;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.post;

class UrlShortener {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortener.class);

    private static final String BASE_URL = "/gateway/shortener/api/v1";

    private final String targetUrl;
    private final OkHttpClient okHttpClient;

    UrlShortener(String targetUrl, OkHttpClient okHttpClient) {
        this.targetUrl = targetUrl;
        this.okHttpClient = okHttpClient;
    }

    void routes() {
        post(BASE_URL, (req, res) -> {
            String service = req.headers("authenticated-service");
            LOGGER.info("Sending post request from service {} to URL shortener", service);
            try (Response response = okHttpClient.newCall(new Request.Builder()
                    .url(targetUrl)
                    .post(RequestBody.create(req.body(), MediaType.parse("application/json")))
                    .build()).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String urlResponse = response.body().string();
                    LOGGER.info("Response from URL shortener is successful {}", urlResponse);
                    res.status(response.code());
                    res.type(response.header("Content-Type", "application/json"));
                    res.body(urlResponse);
                    return urlResponse;
                }
            } catch (Exception e) {
                LOGGER.error("Error while sending request to URL shortener from {}", service);
            }
            return null;
        });
    }
}
