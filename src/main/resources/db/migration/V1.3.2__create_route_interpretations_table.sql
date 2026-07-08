CREATE TABLE IF NOT EXISTS route_interpretations_tb (
    route_interpretation_id UUID PRIMARY KEY,
    route_id UUID NOT NULL,
    interpretation TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_route_interpretations_route FOREIGN KEY (route_id) REFERENCES routes_tb(route_id)
);
