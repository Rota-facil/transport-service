CREATE TABLE IF NOT EXISTS ignored_institutions_tb (
    institution_id UUID NOT NULL,
    trip_id UUID NOT NULL,

    PRIMARY KEY (institution_id, trip_id),

    CONSTRAINT fk_ignored_institutions_institutions FOREIGN KEY (institution_id) REFERENCES institutions_tb(institution_id),
    CONSTRAINT fk_ignored_institutions_trips FOREIGN KEY (trip_id) REFERENCES trips_tb(trip_id)
);