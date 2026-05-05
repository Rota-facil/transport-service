CREATE TABLE IF NOT EXISTS trips_tb (
    trip_id UUID PRIMARY KEY,
    bus_id UUID NOT NULL,
    route_id UUID NOT NULL,
    latitude DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    longitude DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    reason_of_cancellation TEXT,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,

    CONSTRAINT fk_trips_bus FOREIGN KEY (bus_id) REFERENCES bus_tb(bus_id),
    CONSTRAINT fk_trips_routes FOREIGN KEY (route_id) REFERENCES routes_tb(route_id)
);