package test;

//id, shortUrl, longUrl
public interface URLRepository {

    Long saveLongURL(String url);
    void saveShortURL(Long id, String url);

}
