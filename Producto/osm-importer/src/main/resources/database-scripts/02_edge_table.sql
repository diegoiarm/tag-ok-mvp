CREATE TABLE IF NOT EXISTS edge (
    id BIGSERIAL PRIMARY KEY,

    element_id BIGINT UNIQUE,

    name VARCHAR(255),
    type VARCHAR(255),
    surface VARCHAR(255),

    lanes INTEGER,

    maxspeed INTEGER,
    maxspeed_bus INTEGER,
    maxspeed_hgv INTEGER,

    maxweight DOUBLE PRECISION,

    oneway INTEGER,

    geometry geometry(LineString, 4326),

    cost DOUBLE PRECISION,
    reverse_cost DOUBLE PRECISION,

    source INTEGER,
    target INTEGER
);