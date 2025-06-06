package url.shortener

import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class UrlBase62ShortenerIntegrationSpec extends BaseContainerIntegrationSpec {

    @Shared
    UrlShortener shortener

    def setupSpec() {
        shortener = new UrlBase62Shortener(
                new UrlSqlRepositoryImpl(context, jedisPool),
                new Base62Encoder()
        )
    }

    def "should persist and return shorten url when shortening long url"() {
        given:
        def longUrl = "https://medium.com/@stefanovskyi/unit-test-naming-conventions-dd9208eadbea"

        when:
        def actual = shortener.shorten(longUrl);

        then:
        "KSxf" == actual
    }
}

