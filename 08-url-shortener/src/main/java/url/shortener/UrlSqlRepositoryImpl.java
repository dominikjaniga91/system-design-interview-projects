package url.shortener;

import data.tables.Urls;
import data.tables.records.UrlsRecord;
import org.jooq.DSLContext;

import java.util.Optional;

class UrlSqlRepositoryImpl implements UrlRepository {

    private final DSLContext dslContext;

    UrlSqlRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Integer saveLongURL(String url) {
        UrlsRecord record = dslContext.newRecord(Urls.URLS);
        record.setLongUrl(url);
        record.store();
        return record.getId();
    }

    @Override
    public void saveShortURL(Integer id, String url) {
        dslContext.update(Urls.URLS)
                .set(Urls.URLS.SHORT_URL, url)
                .where(Urls.URLS.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<String> findShortUrl(String url) {
        return Optional.ofNullable(dslContext.select(Urls.URLS.SHORT_URL)
                .from(Urls.URLS)
                .where(Urls.URLS.LONG_URL.eq(url))
                .fetchOne(Urls.URLS.SHORT_URL));
    }
}
