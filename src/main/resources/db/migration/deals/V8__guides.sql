CREATE TABLE IF NOT EXISTS deals.guides (
    guide_id   INT NOT NULL PRIMARY KEY,
    type       VARCHAR(100) NOT NULL,
    quote_id   INT NULL,

    CONSTRAINT fk_guides_quote
        FOREIGN KEY (quote_id)
        REFERENCES deals.quote(quote_id)
);
