ALTER TABLE deals.guides
ADD CONSTRAINT uq_guides_type_quote
    UNIQUE (type, quote_id);