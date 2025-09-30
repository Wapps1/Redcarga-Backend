-- catálogo de roles del sistema
CREATE TABLE IF NOT EXISTS iam.system_roles (
    system_role_id   SERIAL PRIMARY KEY,
    code             VARCHAR(40) NOT NULL UNIQUE,
    display_name     VARCHAR(80) NOT NULL,
    description      VARCHAR(200)
    );

-- cuentas
CREATE TABLE IF NOT EXISTS iam.accounts (
    account_id       SERIAL PRIMARY KEY,
    system_role_id   INT NOT NULL REFERENCES iam.system_roles(system_role_id),
    external_uid     VARCHAR(128) NOT NULL UNIQUE,
    email            CITEXT NOT NULL UNIQUE,
    email_verified   BOOLEAN NOT NULL DEFAULT FALSE,
    username         CITEXT NOT NULL UNIQUE,
    status           VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE','PENDING','BANNED')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
    );

-- sesiones
CREATE TABLE IF NOT EXISTS iam.sessions (
    session_id       SERIAL PRIMARY KEY,
    account_id       INT NOT NULL REFERENCES iam.accounts(account_id),
    platform         VARCHAR(16) NOT NULL,  -- ANDROID/IOS/WEB o lo que uses
    status           VARCHAR(16) NOT NULL CHECK (status IN ('ACTIVE','REVOKED','EXPIRED')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_seen_at     TIMESTAMPTZ,
    expires_at       TIMESTAMPTZ,
    ip_address       INET
    );

CREATE INDEX IF NOT EXISTS idx_sessions_account_status
    ON iam.sessions(account_id, status);
