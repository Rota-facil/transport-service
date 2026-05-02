CREATE TABLE IF NOT EXISTS board_points_tb (
    board_point_id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    geom geography NOT NULL
)