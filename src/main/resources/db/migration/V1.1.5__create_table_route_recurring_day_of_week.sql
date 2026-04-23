CREATE TABLE IF NOT EXISTS route_recurring_day_of_week_tb (
    route_id UUID NOT NULL,
    days_of_week VARCHAR(50) NOT NULL,

    CONSTRAINT fk_route_recurring_day_week_route_recurring FOREIGN KEY (route_id) REFERENCES route_recurring_tb(route_id) ON DELETE CASCADE
);