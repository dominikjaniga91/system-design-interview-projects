package url.shortener;

import java.util.Optional;

class UrlBase62Shortener implements UrlShortener {

    private final UrlRepository urlRepository;
    private final BaseEncoder baseEncoder;

    UrlBase62Shortener(UrlRepository urlRepository, BaseEncoder baseEncoder) {
        this.urlRepository = urlRepository;
        this.baseEncoder = baseEncoder;
    }

    @Override
    public String shorten(String url) {
        Optional<String> foundShortUrl = urlRepository.findShortUrl(url);
        if (foundShortUrl.isPresent()) {
            return foundShortUrl.get();
        }
        Integer urlId = urlRepository.saveLongURL(url);
        String shortUrl = baseEncoder.encode(urlId);
        urlRepository.saveShortURL(urlId, shortUrl);
        return shortUrl;
    }
}
