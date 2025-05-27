package url.shortener;

import java.util.Optional;

class InMemoryUrlRepository implements UrlRepository {

    @Override
    public Integer saveLongURL(String url) {
        return 0;
    }

    @Override
    public void saveShortURL(Integer id, String shortUrl, String longUrl) {

    }

    @Override
    public Optional<String> findShortUrl(String url) {
        return Optional.empty();
    }

    @Override
    public String findLongUrl(String url) {
        return "http://localhost:4567/shortener/api/v1/test";
    }
}
