package url.shortener;

import com.google.gson.Gson;

import static spark.Spark.get;
import static spark.Spark.post;

public class UrlController {

    private static final String BASE_URL = "/shortener/api/v1";
    private static final Gson GSON = new Gson();

    private final UrlShortener urlShortener;
    private final UrlRepository urlRepository;

    UrlController(UrlShortener urlShortener, UrlRepository urlRepository) {
        this.urlShortener = urlShortener;
        this.urlRepository = urlRepository;
    }

    void routes() {
        post(BASE_URL, (req, res) -> {
            Url url = GSON.fromJson(req.body(), Url.class);
            return String.format("%s/%s", req.url(), urlShortener.shorten(url.getUrlValue()));
        });

        get(BASE_URL + "/:shortUrl", (req, res) -> {
            String longUrl = urlRepository.findLongUrl(req.params(":shortUrl"));
            res.redirect(longUrl);
            return longUrl;
        });
    }
}
