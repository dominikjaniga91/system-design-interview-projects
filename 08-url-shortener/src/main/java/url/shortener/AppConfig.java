package url.shortener;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

class AppConfig {

    private final String username;
    private final String password;
    private final String url;

    private AppConfig(String username, String password, String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    static AppConfig load() {
        Yaml yaml = new Yaml();
        try (var inputStream = AppConfig.class.getClassLoader().getResourceAsStream("configuration.yaml")) {
            Map<String, Object> config = yaml.load(inputStream);
            return new AppConfig(config.get("username").toString(), config.get("password").toString(), config.get("url").toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }
}
