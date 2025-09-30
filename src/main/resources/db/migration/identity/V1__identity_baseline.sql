-- =========================================================
-- SCHEMA
-- =========================================================
CREATE SCHEMA IF NOT EXISTS identity;

-- =========================================================
-- CATÁLOGOS
-- =========================================================
CREATE TABLE IF NOT EXISTS identity.doc_types (
                                                  doc_type_id   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                  code          VARCHAR(10) UNIQUE,
    name          VARCHAR(60) NOT NULL
    );

CREATE TABLE IF NOT EXISTS identity.kyc_status (
                                                   kyc_status_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                   code          VARCHAR(20) UNIQUE,
    name          VARCHAR(60) NOT NULL
    );

-- =========================================================
-- PERSONS (se crea sólo cuando pasa validaciones)
-- =========================================================
CREATE TABLE IF NOT EXISTS identity.persons (
                                                person_id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                account_id   INT NOT NULL UNIQUE
                                                REFERENCES iam.accounts(account_id) ON DELETE CASCADE,

    full_name    VARCHAR(150) NOT NULL,
    birth_date   DATE NOT NULL,
    doc_type_id  INT  NOT NULL
    REFERENCES identity.doc_types(doc_type_id),
    doc_number   VARCHAR(32) NOT NULL,

    created_at   TIMESTAMPTZ NOT NULL,   -- lo setea la app
    updated_at   TIMESTAMPTZ NOT NULL,   -- lo setea la app

    CONSTRAINT uq_person_doc UNIQUE (doc_type_id, doc_number)
    );

CREATE INDEX IF NOT EXISTS ix_persons_doc_type ON identity.persons(doc_type_id);

-- =========================================================
-- KYC VERIFICATIONS (intentos; sin evidencias)
-- =========================================================
CREATE TABLE IF NOT EXISTS identity.kyc_verifications (
                                                          verification_id     INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                          person_id           INT NOT NULL
                                                          REFERENCES identity.persons(person_id) ON DELETE CASCADE,

    provider_code       VARCHAR(40) NOT NULL,          -- 'LOCAL' hoy; 'VENDOR_X' mañana
    session_id          VARCHAR(100) NOT NULL UNIQUE,  -- UUID propio o del vendor

    kyc_status_id       INT NOT NULL
    REFERENCES identity.kyc_status(kyc_status_id),

    validated_at        TIMESTAMPTZ,                   -- lo setea la app
    liveness_passed     BOOLEAN,
    returned_full_name  VARCHAR(150),
    returned_birth_date DATE,

    evidence_stored     BOOLEAN NOT NULL DEFAULT FALSE,

    created_at          TIMESTAMPTZ NOT NULL,          -- lo setea la app
    updated_at          TIMESTAMPTZ NOT NULL           -- lo setea la app
    );

CREATE INDEX IF NOT EXISTS ix_kyc_ver_person ON identity.kyc_verifications(person_id);
CREATE INDEX IF NOT EXISTS ix_kyc_ver_status ON identity.kyc_verifications(kyc_status_id);

-- =========================================================
-- VERIFICATION ATTEMPTS (auditoría append-only)
-- =========================================================
CREATE TABLE IF NOT EXISTS identity.verification_attempts (
                                                              attempt_id      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                              verification_id INT NOT NULL
                                                              REFERENCES identity.kyc_verifications(verification_id) ON DELETE CASCADE,

    provider_code   VARCHAR(40) NOT NULL,   -- 'LOCAL', 'VENDOR_X', etc.
    request_meta    JSONB,                  -- metadatos mínimos (sin PII cruda)
    result          VARCHAR(10) NOT NULL CHECK (result IN ('OK','FAIL')),
    response_code   VARCHAR(50),

    created_at      TIMESTAMPTZ NOT NULL    -- lo setea la app
    );

CREATE INDEX IF NOT EXISTS ix_attempts_ver ON identity.verification_attempts(verification_id);
