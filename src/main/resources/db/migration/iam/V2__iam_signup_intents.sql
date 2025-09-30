-- Crear tabla de intents de registro en el esquema IAM
-- PK entero (IDENTITY) para alinearse con tus aggregates base.

CREATE SCHEMA IF NOT EXISTS iam;


CREATE TABLE IF NOT EXISTS iam.signup_intents (
    signup_intent_id          INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id                INTEGER NOT NULL
    REFERENCES iam.accounts(account_id) ON DELETE CASCADE,


    status                    VARCHAR(40) NOT NULL
    CHECK (status IN (
           'PENDING_EMAIL_VERIFICATION',
           'EMAIL_VERIFIED',
           'BASIC_PROFILE_COMPLETED',
           'DONE'
                     )),

    expires_at                TIMESTAMPTZ NOT NULL,
    last_step_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    verification_sent_count   INTEGER NOT NULL DEFAULT 0,
    last_verification_sent_at TIMESTAMPTZ NULL,

    platform                  VARCHAR(16) NULL,   -- WEB | ANDROID | IOS (texto libre)
    meta                      JSONB NULL,         -- datos auxiliares (continueUrl, etc.)
    idempotency_key           VARCHAR(64) NULL UNIQUE,

    created_at                TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE UNIQUE INDEX IF NOT EXISTS ux_signup_intent_open
    ON iam.signup_intents (account_id)
    WHERE status IN ('PENDING_EMAIL_VERIFICATION','EMAIL_VERIFIED','BASIC_PROFILE_COMPLETED');

-- Índices útiles
CREATE INDEX IF NOT EXISTS ix_signup_intents_account ON iam.signup_intents (account_id);
CREATE INDEX IF NOT EXISTS ix_signup_intents_status  ON iam.signup_intents (status);
CREATE INDEX IF NOT EXISTS ix_signup_intents_expires ON iam.signup_intents (expires_at);
