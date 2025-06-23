package gateway;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;

class AppConfig {

    private final URLs urls;

    private AppConfig(URLs urIs) {
        urls = urIs;
    }

    public static AppConfig load() {
        String profile = System.getProperty("profile");
        Yaml yaml = new Yaml();
        try (var inputStream = AppConfig.class.getClassLoader().getResourceAsStream(format("configuration-%s.yaml", profile))) {
            if (inputStream == null) {
                throw new RuntimeException("Missing configuration.yaml in resources");
            }

            Map<String, Object> config = yaml.load(inputStream);
            return new AppConfig(new URLs((String) config.get("urlShortenerUri")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public String getUrlsShortenerUri() {
        return urls.urlShortenerUri;
    }

    private record URLs(String urlShortenerUri) {
    }
}
