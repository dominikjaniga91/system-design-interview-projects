package gateway;

import static spark.Spark.port;

class Server {

    private final UrlShortener urlShortener;

    Server(UrlShortener urlShortener) {
        this.urlShortener = urlShortener;
    }

    public void start() {
        port(4500);
        urlShortener.routes();
    }
}