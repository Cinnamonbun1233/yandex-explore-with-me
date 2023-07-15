CREATE TABLE IF NOT EXISTS records (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(100),
    uri VARCHAR(100),
    ip VARCHAR(100),
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);