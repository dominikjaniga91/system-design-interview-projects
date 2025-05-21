package url.shortener;

import com.google.gson.Gson;

import static spark.Spark.*;

class Server {

    private final UrlShortener urlShortener;

    Server(UrlShortener urlShortener) {
        this.urlShortener = urlShortener;
    }

    public void start() {
        post("/shortener/api/v1", (req, res) -> {
            Url url = new Gson().fromJson(req.body(), Url.class);
            return String.format("%s/%s", req.url(), urlShortener.shorten(url.getUrlValue()));
        });
    }
}