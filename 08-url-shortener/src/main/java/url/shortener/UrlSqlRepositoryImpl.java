package url.shortener;

import data.tables.Urls;
import data.tables.records.UrlsRecord;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;

class UrlSqlRepositoryImpl implements UrlRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlSqlRepositoryImpl.class);

    private final DSLContext dslContext;
    private final JedisPool jedisPool;

    UrlSqlRepositoryImpl(DSLContext dslContext, JedisPool jedisPool) {
        this.dslContext = dslContext;
        this.jedisPool = jedisPool;
    }

    @Override
    public Integer saveLongURL(String url) {
        try {
            UrlsRecord record = dslContext.newRecord(Urls.URLS);
            record.setLongUrl(url);
            record.store();
            Integer id = record.getId();
            LOGGER.info("Url has been saved in database. ID: - {}", id);
            return id;
        } catch (Exception e) {
            LOGGER.info("Error while saving long URL - {}", url);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveShortURL(Integer id, String shortUrl, String longUrl) {
        LOGGER.info("Saving short URL with id {}", id);
        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set(shortUrl, longUrl);
            LOGGER.info("URL persisted in cache {}", result);

            dslContext.update(Urls.URLS)
                    .set(Urls.URLS.SHORT_URL, shortUrl)
                    .where(Urls.URLS.ID.eq(id))
                    .execute();
            LOGGER.info("URL persisted in database");
        } catch (Exception e) {
            LOGGER.error("Short URL saving failed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<String> findShortUrl(String longUrl) {
        LOGGER.info("Finding short URL - {}", longUrl);
        try {
            return dslContext.select(Urls.URLS.SHORT_URL)
                    .from(Urls.URLS)
                    .where(Urls.URLS.LONG_URL.eq(longUrl))
                    .fetchOptional(Urls.URLS.SHORT_URL);
        } catch (Exception e) {
            LOGGER.error("Error while getting short URL - {}", longUrl, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String findLongUrl(String shortUrl) {
        LOGGER.info("Finding long URL. Short URL: {}", shortUrl);
        try (Jedis jedis = jedisPool.getResource()) {
            String cachedUrl = jedis.get(shortUrl);
            if (cachedUrl != null) {
                LOGGER.info("Long URL found in cache {}", cachedUrl);
                return cachedUrl;
            }

            return dslContext.select()
                    .from(Urls.URLS)
                    .where(Urls.URLS.SHORT_URL.eq(shortUrl))
                    .fetchOptional(Urls.URLS.LONG_URL)
                    .orElseThrow(() -> new RuntimeException("Cannot find long URL in database"));
        } catch (Exception e) {
            LOGGER.error("Error while getting long URL - {}", shortUrl, e);
            throw new RuntimeException(e);
        }
    }
}
