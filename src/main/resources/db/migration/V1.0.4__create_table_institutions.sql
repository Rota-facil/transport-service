CREATE TABLE IF NOT EXISTS institutions_tb (
    institution_id UUID PRIMARY KEY,
    name VARCHAR(100),
    latitude TEXT NOT NULL,
    longitude TEXT NOT NULL
);