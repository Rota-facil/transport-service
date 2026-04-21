CREATE TABLE IF NOT EXISTS routes_tb (
    route_id UUID PRIMARY KEY,
    shift VARCHAR(20) NOT NULL,
    going TIME NOT NULL,
    return TIME NOT NULL,
    going_finish TIME NOT NULL,
    return_finish TIME NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);