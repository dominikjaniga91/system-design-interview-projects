package test;

import org.jooq.DSLContext;

//id, shortUrl, longUrl
public class URLRepositoryImpl implements URLRepository {

    private final DSLContext dslContext;

    public URLRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Long saveLongURL(String url) {
//        dslContext.newRecord(Tables.URLS);
        return 0L;
    }

    @Override
    public void saveShortURL(Long id, String url) {

    }
}
