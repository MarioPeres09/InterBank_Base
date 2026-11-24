ALTER TABLE transfers ADD COLUMN last_attempt_at TIMESTAMPTZ NOT NULL DEFAULT now();

CREATE INDEX transfers_pending_retry_idx
    ON transfers (last_attempt_at, id)
    WHERE status = 'PENDING';
