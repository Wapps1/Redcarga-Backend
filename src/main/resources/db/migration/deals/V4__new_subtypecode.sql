INSERT INTO deals.catalog_chat_subtype(code)
VALUES ('QUOTE_REJECTED')
ON CONFLICT (code) DO NOTHING;