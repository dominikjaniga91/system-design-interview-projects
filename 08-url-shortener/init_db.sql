CREATE TABLE urls
(
    id        serial PRIMARY KEY,
    short_url VARCHAR DEFAULT NULL,
    long_url  VARCHAR DEFAULT NULL UNIQUE
);