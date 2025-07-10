package gateway;

public class App {

    public static void main(String[] args) {
        var config = AppConfig.load();
        var dependencies = new AppDependencies(config);
        new Server(dependencies).start();
    }
}
