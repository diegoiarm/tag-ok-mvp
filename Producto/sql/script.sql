CREATE EXTENSION IF NOT EXISTS postgis;

CREATE EXTENSION IF NOT EXISTS pgrouting;

CREATE TABLE
    IF NOT EXISTS edge (
        id SERIAL PRIMARY KEY,
        element_id BIGINT UNIQUE, -- ID original de OSM
        name TEXT, -- Nombre de la calle
        type TEXT, -- Tipo (motorway, residential, etc.)
        surface TEXT, -- Superficie (asphalt, concrete)
        lanes INTEGER, -- Cantidad de pistas
        maxspeed INTEGER, -- Velocidad máxima
        maxspeed_bus INTEGER,
        maxspeed_hgv INTEGER,
        maxweight FLOAT, -- Restricción de peso
        oneway INTEGER, -- 1=Sentido único, 0=Doble sentido
        geometry GEOMETRY (LineString, 4326),
        source INTEGER, -- Nodo inicial (llenado por pgr_createTopology)
        target INTEGER, -- Nodo final (llenado por pgr_createTopology)
        cost FLOAT8, -- Costo de ida (longitud o tiempo)
        reverse_cost FLOAT8 -- Costo de vuelta (-1 si es oneway)
    );

-- Índice espacial GIST: Vital para que las búsquedas geográficas sean rápidas
CREATE INDEX IF NOT EXISTS edge_gix ON edge USING GIST (geometry);

-- Índice en element_id: Acelera el ON CONFLICT durante la carga masiva
CREATE INDEX IF NOT EXISTS edge_id2_idx ON edge (element_id);

CREATE TABLE
    IF NOT EXISTS portico (
        id SERIAL PRIMARY KEY,
        codigo TEXT UNIQUE,
        geometry GEOMETRY (Point, 4326)
    );

CREATE TABLE 
    IF NOT EXISTS edge_portico (
        id SERIAL PRIMARY KEY,
        edge_id INTEGER REFERENCES edge(id),
        portico_id INTEGER REFERENCES portico(id),
        fraction DOUBLE PRECISION
    );

CREATE TABLE 
    IF NOT EXISTS precio_portico (
        id SERIAL PRIMARY KEY,
        portico_id INTEGER REFERENCES portico(id),
        tipo_vehiculo TEXT,
        precio INTEGER
    );

CREATE TABLE IF NOT EXISTS tipo_tarifa (
    id SERIAL PRIMARY KEY,
    codigo TEXT UNIQUE  -- TBFP, TBP
);

CREATE TABLE IF NOT EXISTS tipo_vehiculo (
    id SERIAL PRIMARY KEY,
    categoria TEXT UNIQUE, -- "1 y 4", "2", "3"
    tipo TEXT UNIQUE -- "automóvil", "camión", etc.
);

CREATE TABLE IF NOT EXISTS tarifa_portico (
    id SERIAL PRIMARY KEY,
    portico_id INTEGER REFERENCES portico(id),
    tipo_tarifa_id INTEGER REFERENCES tipo_tarifa(id),
    tipo_vehiculo_id INTEGER REFERENCES tipo_vehiculo(id),
    precio NUMERIC(10,2)
);

CREATE TABLE IF NOT EXISTS horario_tarifa (
    id SERIAL PRIMARY KEY,
    tipo_tarifa_id INTEGER REFERENCES tipo_tarifa(id),
    dia TEXT,  -- "Laboral", "Sabado y Festivo"
    hora_inicio TIME,
    hora_fin TIME
);

CREATE TABLE IF NOT EXISTS autopista (
    id SERIAL PRIMARY KEY,
    nombre TEXT UNIQUE
);

ALTER TABLE portico
ADD COLUMN autopista_id INTEGER REFERENCES autopista(id);

-- falta el horario

CREATE INDEX portico_gix ON portico USING GIST (geometry);

CREATE INDEX idx_edge_portico_edge ON edge_portico(edge_id);
CREATE INDEX idx_edge_portico_portico ON edge_portico(portico_id);

CREATE INDEX idx_precio_portico_portico ON precio_portico(portico_id);
CREATE INDEX idx_precio_horario_precio ON precio_horario(precio_portico_id);