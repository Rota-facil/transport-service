CREATE TABLE IF NOT EXISTS ignored_board_points_tb (
    trip_id UUID NOT NULL,
    board_point_id UUID NOT NULL,

    PRIMARY KEY (trip_id, board_point_id),
    CONSTRAINT fk_ignored_board_points_trips FOREIGN KEY (trip_id) REFERENCES trips_tb(trip_id),
    CONSTRAINT fk_ignored_board_points_board_points FOREIGN KEY (board_point_id) REFERENCES board_points_tb(board_point_id)
);