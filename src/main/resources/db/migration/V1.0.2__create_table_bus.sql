CREATE TABLE IF NOT EXISTS bus_tb (
    bus_id UUID PRIMARY KEY,
    driver_id UUID NOT NULL,
    prefecture_id UUID NOT NULL,
    capacity BIGINT NOT NULL,
    plate VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT bus_users FOREIGN KEY (driver_id) REFERENCES users_tb(user_id)
);