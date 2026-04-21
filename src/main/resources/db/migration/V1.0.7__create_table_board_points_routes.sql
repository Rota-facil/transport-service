CREATE TABLE IF NOT EXISTS board_points_routes_tb (
    board_point_route_id UUID PRIMARY KEY,
    board_point_id UUID NOT NULL,
    route_id UUID NOT NULL,
    board_time_going TIME NOT NULL,
    board_time_finish TIME NOT NULL,

    CONSTRAINT u_board_point_route_id UNIQUE (board_point_id, route_id),
    CONSTRAINT fk_board_points_routes_board_points FOREIGN KEY (board_point_id) REFERENCES board_points_tb(board_point_id),
    CONSTRAINT fk_board_points_routes_routes FOREIGN KEY (route_id) REFERENCES routes_tb(route_id)
);