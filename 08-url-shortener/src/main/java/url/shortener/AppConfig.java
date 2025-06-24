package url.shortener;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;

class AppConfig {

    private final DatabaseConfig database;
    private final JedisConfig jedis;

    private AppConfig(DatabaseConfig database, JedisConfig jedis) {
        this.database = database;
        this.jedis = jedis;
    }

    public static AppConfig load() {
        String profile = System.getProperty("profile");
        Yaml yaml = new Yaml();
        try (var inputStream = AppConfig.class.getClassLoader().getResourceAsStream(format("configuration-%s.yaml", profile))) {
            if (inputStream == null) {
                throw new RuntimeException("Missing configuration.yaml in resources");
            }

            Map<String, Object> config = yaml.load(inputStream);
            return new AppConfig(
                    new DatabaseConfig((Map<String, Object>) config.get("database")),
                    new JedisConfig((Map<String, Object>) config.get("jedis"))
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    String getUsername() { return database.username; }
    String getPassword() { return database.password; }
    String getUrl() { return database.url; }
    String getJedisHost() { return jedis.host; }
    int getJedisPort() { return jedis.port; }

    private static class DatabaseConfig {
        private final String username;
        private final String password;
        private final String url;

        private DatabaseConfig(Map<String ,Object> config) {
            username = config.get("username").toString();
            password = config.get("password").toString();
            url = config.get("url").toString();;
        }
    }

    private static class JedisConfig {
        private final String host;
        private final int port;

        private JedisConfig(Map<String ,Object> config) {
            this.host = config.get("host").toString();
            this.port = Integer.parseInt(config.get("port").toString());
        }
    }
}
