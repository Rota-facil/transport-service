CREATE TABLE IF NOT EXISTS route_recurring_tb (
    route_recurring_id UUID PRIMARY KEY,
    route_id UUID NOT NULL,
    bus_id UUID NOT NULL,

    CONSTRAINT u_route_id UNIQUE(route_id),
    CONSTRAINT fk_route_recurring_route FOREIGN KEY (route_id) REFERENCES routes_tb(route_id) ON DELETE CASCADE,
    CONSTRAINT fk_route_recurring_bus FOREIGN KEY (bus_id) REFERENCES bus_tb(bus_id)
);