ALTER TABLE bus_tb ALTER COLUMN driver_id DROP NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_bus_driver_id_not_null
ON bus_tb(driver_id)
WHERE driver_id IS NOT NULL;
