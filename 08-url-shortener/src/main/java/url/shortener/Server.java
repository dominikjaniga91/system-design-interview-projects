package url.shortener;

class Server {

    private final UrlController urlController;

    Server(UrlController urlController) {
        this.urlController = urlController;
    }

    public void start() {
        urlController.routes();
    }
}