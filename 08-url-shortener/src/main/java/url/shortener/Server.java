package url.shortener;

import com.google.gson.Gson;

import static spark.Spark.*;

class Server {

    private static final String BASE_URL = "/shortener/api/v1";

    private final UrlShortener urlShortener;
    private final UrlRepository urlRepository;

    Server(UrlShortener urlShortener, UrlRepository urlRepository) {
        this.urlShortener = urlShortener;
        this.urlRepository = urlRepository;
    }

    public void start() {
        post(BASE_URL, (req, res) -> {
            Url url = new Gson().fromJson(req.body(), Url.class);
            return String.format("%s/%s", req.url(), urlShortener.shorten(url.getUrlValue()));
        });

        get(BASE_URL + "/:shortUrl", (req, res) -> {
            String longUrl = urlRepository.findLongUrl(req.params(":shortUrl"));
            res.redirect(longUrl);
            return null;
        });
    }
}