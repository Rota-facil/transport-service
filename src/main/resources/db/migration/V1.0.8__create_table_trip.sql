CREATE TABLE IF NOT EXISTS trips_tb (
    trip_id UUID PRIMARY KEY,
    bus_id UUID NOT NULL,
    route_id UUID NOT NULL,
    reason_of_cancellation TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trips_bus FOREIGN KEY (bus_id) REFERENCES bus_tb(bus_id),
    CONSTRAINT fk_trips_routes FOREIGN KEY (route_id) REFERENCES routes_tb(route_id)
);