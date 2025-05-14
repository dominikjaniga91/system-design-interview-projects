CREATE TABLE urls
(
    id        serial PRIMARY KEY,
    short_url VARCHAR(50) NOT NULL,
    long_url  VARCHAR     NOT NULL
);