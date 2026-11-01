CREATE TABLE trade(
    int         BIGSERIAL PRIMARY KEY,
    trade_id    VARCHAR(64)     NOT NULL UNIQUE, -- idempotency backstop
    trade_type  VARCHAR(16)     NOT NULL,
    instrument  VARCHAR(32)     NOT NULL,
    side        VARCHAR(4)      NOT NULL,
    quantity    NUMERIC(18, 4)  NOT NULL,
    price       NUMERIC(18, 6)  NOT NULL,
    currency    CHAR(3)         NOT NULL,
    trade_date  DATE            NOT NULL,
    status      VARCHAR(16)     NOT NULL DEFAULT 'RECEIVED',
    source      VARCHAR(16)     NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE INDEX idx_trade_status ON trade (status);