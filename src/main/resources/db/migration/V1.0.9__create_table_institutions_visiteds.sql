CREATE TABLE IF NOT EXISTS institutions_visiteds_tb (
    institution_visited_id UUID PRIMARY KEY,
    trip_id UUID NOT NULL,
    institution_id UUID NOT NULL,
    going BOOLEAN NOT NULL DEFAULT FALSE,
    return BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_institutions_visiteds_trips FOREIGN KEY (trip_id) REFERENCES trips_tb(trip_id),
    CONSTRAINT fk_institutions_visiteds_institutions FOREIGN KEY (institution_id) REFERENCES institutions_tb(institution_id)
);