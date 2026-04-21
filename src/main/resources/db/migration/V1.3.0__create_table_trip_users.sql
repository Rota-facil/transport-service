CREATE TABLE IF NOT EXISTS trip_users_tb (
    trip_user_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    trip_id UUID NOT NULL,
    institution_id UUID NOT NULL,
    board_point_id UUID NOT NULL,
    present BOOLEAN NOT NULL DEFAULT FALSE,
    going BOOLEAN NOT NULL DEFAULT FALSE,
    return BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT u_user_trip UNIQUE (user_id, trip_id),

    CONSTRAINT fk_trip_users_users FOREIGN KEY (user_id) REFERENCES users_tb(user_id),
    CONSTRAINT fk_trip_users_trips FOREIGN KEY (trip_id) REFERENCES trips_tb(trip_id),
    CONSTRAINT fk_trip_users_institutions FOREIGN KEY (institution_id) REFERENCES institutions_tb(institution_id)
);