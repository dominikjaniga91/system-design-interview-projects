package url.shortener;

import com.typesafe.config.Config;

import static com.typesafe.config.ConfigFactory.defaultOverrides;
import static com.typesafe.config.ConfigFactory.parseResourcesAnySyntax;

class AppConfig {

    private final Config config;

    private AppConfig(Config config) {
        this.config = config;
    }

    public static AppConfig load() {
        String configFileName = String.format("%s.conf", System.getProperty("profile"));
        Config config = defaultOverrides()
                .withFallback(parseResourcesAnySyntax(configFileName))
                .resolve();

        return new AppConfig(config);
    }

    String getUsername() {
        return config.getString("database.username");
    }

    String getPassword() {
        return config.getString("database.password");
    }

    String getUrl() {
        return config.getString("database.url");
    }

    String getJedisHost() {
        return config.getString("jedis.host");
    }

    int getJedisPort() {
        return config.getInt("jedis.port");
    }
}
