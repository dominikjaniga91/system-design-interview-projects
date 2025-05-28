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

            var dbMap = (Map<String, Object>) config.get("database");
            var jedisMap = (Map<String, Object>) config.get("jedis");

            DatabaseConfig db = new DatabaseConfig(
                    dbMap.get("username").toString(),
                    dbMap.get("password").toString(),
                    dbMap.get("url").toString()
            );

            JedisConfig jedis = new JedisConfig(
                    jedisMap.get("host").toString(),
                    (int) jedisMap.get("port")
            );

            return new AppConfig(db, jedis);

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

        private DatabaseConfig(String username, String password, String url) {
            this.username = username;
            this.password = password;
            this.url = url;
        }
    }

    private static class JedisConfig {
        private final String host;
        private final int port;

        private JedisConfig(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}
