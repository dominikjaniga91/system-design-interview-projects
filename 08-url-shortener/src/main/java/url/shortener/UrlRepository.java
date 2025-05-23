package url.shortener;

import java.util.Optional;

public interface UrlRepository {

    Integer saveLongURL(String url);

    void saveShortURL(Integer id, String shortUrl, String longUrl);

    Optional<String> findShortUrl(String url);
}
