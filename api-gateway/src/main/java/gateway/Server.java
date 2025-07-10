package gateway;

import static spark.Spark.before;
import static spark.Spark.port;

class Server {


    private final AppDependencies dependencies;

    Server(AppDependencies dependencies) {
        this.dependencies = dependencies;
    }

    public void start() {
        port(4500);
        before(dependencies.getRateLimitFilter());
        dependencies.getUrlShortener().routes();
    }
}