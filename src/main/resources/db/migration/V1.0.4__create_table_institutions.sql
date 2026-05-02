CREATE TABLE IF NOT EXISTS institutions_tb (
    institution_id UUID PRIMARY KEY,
    name VARCHAR(100),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    geom geography NOT NULL
);