-- Asegura esquema
CREATE SCHEMA IF NOT EXISTS iam;

-- Si la tabla ya existe con citext (caso local), convierte tipos a varchar
DO $$
BEGIN
  -- email
  IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema='iam' AND table_name='accounts'
        AND column_name='email'
        AND (data_type='citext' OR udt_name='citext')
  ) THEN
ALTER TABLE iam.accounts
ALTER COLUMN email TYPE VARCHAR(40) USING email::text;
END IF;

  -- username
  IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema='iam' AND table_name='accounts'
        AND column_name='username'
        AND (data_type='citext' OR udt_name='citext')
  ) THEN
ALTER TABLE iam.accounts
ALTER COLUMN username TYPE VARCHAR(40) USING username::text;
END IF;
END
$$;
