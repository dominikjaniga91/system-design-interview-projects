package url.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

class UrlBase62Shortener implements UrlShortener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlBase62Shortener.class);

    private final UrlRepository urlRepository;
    private final BaseEncoder baseEncoder;

    UrlBase62Shortener(UrlRepository urlRepository, BaseEncoder baseEncoder) {
        this.urlRepository = urlRepository;
        this.baseEncoder = baseEncoder;
    }

    @Override
    public String shorten(String longUrl) {
        LOGGER.info("Shortening long URL - {}", longUrl);
        Optional<String> foundShortUrl = urlRepository.findShortUrl(longUrl);
        if (foundShortUrl.isPresent()) {
            return foundShortUrl.get();
        }
        Integer urlId = urlRepository.saveLongURL(longUrl);
        String shortUrl = baseEncoder.encode(urlId);
        urlRepository.saveShortURL(urlId, shortUrl, longUrl);
        LOGGER.info("URL has been shorten");
        return shortUrl;
    }
}
