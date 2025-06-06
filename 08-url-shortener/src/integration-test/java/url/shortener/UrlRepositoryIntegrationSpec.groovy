package url.shortener

import data.tables.Urls
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class UrlRepositoryIntegrationSpec extends BaseContainerIntegrationSpec {

    @Shared
    private UrlRepository urlRepository

    def setupSpec() {
        urlRepository = new UrlSqlRepositoryImpl(context, jedisPool)
    }

    def cleanupSpec() {
        context.deleteFrom(Urls.URLS).execute()
    }

    def "should save long URL in database"() {
        given:
        def longUrl = "https://medium.com/@stefanovskyi/unit-test-naming-conventions-dd9208eadbea"

        when:
        def urlId = urlRepository.saveLongURL(longUrl)

        then:
        urlId != null
        urlId > 0
    }

    def "should save short URL in database"() {
        given:
        def longUrl = "https://mvnrepository.com/artifact/org.testcontainers/testcontainers"
        def id = saveLongUrl(longUrl)
        def shortUrl = "xkv3d5"

        when:
        urlRepository.saveShortURL(id, shortUrl, longUrl)

        then:
        findShortUrl(shortUrl) == shortUrl
    }

    def "should find short URL in database"() {
        given:
        def shortUrl = "gsf4e7"
        def longUrl = "http://www.longUrlgsf4e7.com"
        saveShortAndLongUrl(shortUrl, longUrl)

        when:
        def result = urlRepository.findShortUrl(longUrl)

        then:
        result.present
        result.get() == shortUrl
    }

    def "should find long URL in database"() {
        given:
        def shortUrl = "trn6d7"
        def longUrl = "https://swe.auspham.dev/docs/alexu-system-design-interview/4-design-a-rate-limiter/sliding-window-log-algorithm/"
        saveShortAndLongUrl(shortUrl, longUrl)

        when:
        def actual = urlRepository.findLongUrl(shortUrl)

        then:
        actual == longUrl
    }

    def saveLongUrl(def url) {
        def record = context.newRecord(Urls.URLS)
        record.setLongUrl(url)
        record.store()
        return record.getId()
    }

    void saveShortAndLongUrl(String shortUrl, String longUrl) {
        def record = context.newRecord(Urls.URLS)
        record.setShortUrl(shortUrl)
        record.setLongUrl(longUrl)
        record.store()
    }

    def findShortUrl(String url) {
        context.select(Urls.URLS.SHORT_URL)
                .from(Urls.URLS)
                .where(Urls.URLS.SHORT_URL.eq(url))
                .fetchOne(Urls.URLS.SHORT_URL)
    }
}