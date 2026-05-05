CREATE TABLE IF NOT EXISTS board_points_visiteds_tb (
    board_point_visited_id UUID PRIMARY KEY,
    trip_id UUID NOT NULL,
    board_point_id UUID NOT NULL,
    going BOOLEAN NOT NULL DEFAULT FALSE,
    return BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_board_point_visiteds_trips FOREIGN KEY (trip_id) REFERENCES trips_tb(trip_id),
    CONSTRAINT fk_board_point_visiteds_board_points FOREIGN KEY (board_point_id) REFERENCES board_points_tb(board_point_id)
);