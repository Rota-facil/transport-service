CREATE TABLE IF NOT EXISTS board_points_tb (
    board_point_id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    latitude TEXT NOT NULL,
    longitude TEXT NOT NULL
)