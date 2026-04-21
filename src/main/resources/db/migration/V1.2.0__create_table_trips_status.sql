CREATE TABLE IF NOT EXISTS trip_status_tb (
    trip_status_id UUID PRIMARY KEY,
    trip_id UUID NOT NULL,
    progress VARCHAR(20) NOT NULL,
    delay VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trip_status_trips FOREIGN KEY (trip_id) REFERENCES trips_tb(trip_id)
);