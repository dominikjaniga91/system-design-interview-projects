package url.shortener;

import data.tables.Urls;
import data.tables.records.UrlsRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class UrlRepositoryIntegrationTest extends BaseContainerIntegrationTest {

    private static UrlRepository urlRepository;

    @BeforeAll
    static void setUp() {
        urlRepository = new UrlSqlRepositoryImpl(context, jedisPool);
    }

    @BeforeEach
    void clearDatabase() {
        context.deleteFrom(Urls.URLS).execute();
    }

    @Test
    void shouldSaveLongUrlInDatabase() {
        //given
        String longUrl = "https://medium.com/@stefanovskyi/unit-test-naming-conventions-dd9208eadbea";

        //when
        Integer urlId = urlRepository.saveLongURL(longUrl);

        //then
        Assertions.assertThat(urlId)
                .isNotNull()
                .isPositive();
    }

    @Test
    void shouldSaveShortUrlInDatabase() {
        //given
        String longUrl = "https://mvnrepository.com/artifact/org.testcontainers/testcontainers";
        Integer id = saveLongUrl(longUrl);
        String shortUrl = "xkv3d5";

        //when
        urlRepository.saveShortURL(id, shortUrl, longUrl);

        //then
        Assertions.assertThat(findShortUrl(shortUrl))
                .isNotNull()
                .isEqualTo(shortUrl);
    }

    @Test
    void shouldFindShortUrlInDatabase() {
        //given
        String shortUrl = "gsf4e7";
        String longUrl = "http://www.longUrlgsf4e7.com";
        saveShortAndLongUrl(shortUrl, longUrl);

        //when
        Optional<String> actual = urlRepository.findShortUrl(longUrl);

        //then
        Assertions.assertThat(actual)
                .get()
                .isEqualTo(shortUrl);
    }

    @Test
    void shouldFindLongUrlInDatabase() {
        //given
        String shortUrl = "trn6d7";
        String longUrl = "https://swe.auspham.dev/docs/alexu-system-design-interview/4-design-a-rate-limiter/sliding-window-log-algorithm/";
        saveShortAndLongUrl(shortUrl, longUrl);

        //when
        String actual = urlRepository.findLongUrl(shortUrl);

        //then
        Assertions.assertThat(actual)
                .isEqualTo(longUrl);
    }

    Integer saveLongUrl(String url) {
        UrlsRecord record = context.newRecord(Urls.URLS);
        record.setLongUrl(url);
        record.store();
        return record.getId();
    }

    void saveShortAndLongUrl(String shortUrl, String longUrl) {
        UrlsRecord record = context.newRecord(Urls.URLS);
        record.setShortUrl(shortUrl);
        record.setLongUrl(longUrl);
        record.store();
        record.getId();
    }

    String findShortUrl(String url) {
        return context.select(Urls.URLS.SHORT_URL)
                .from(Urls.URLS)
                .where(Urls.URLS.SHORT_URL.eq(url))
                .fetchOne(Urls.URLS.SHORT_URL);
    }
}
