package gateway;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

import static com.typesafe.config.ConfigFactory.defaultOverrides;
import static com.typesafe.config.ConfigFactory.parseResourcesAnySyntax;
import static java.lang.String.format;

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

    public String getUrlsShortenerUri() {
        return config.getString("api.shortener.url");
    }

    public String getJedisHost() {
        return config.getString("jedis.host");
    }

    public int getJedisPort() {
        return config.getInt("jedis.port");
    }
}
