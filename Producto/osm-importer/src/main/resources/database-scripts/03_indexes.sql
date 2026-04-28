-- espacial para PostGIS
CREATE INDEX IF NOT EXISTS idx_edge_geom
ON edge
USING GIST (geometry);


-- búsquedas por OSM id
CREATE INDEX IF NOT EXISTS idx_edge_element_id
ON edge(element_id);


-- para pgRouting
CREATE INDEX IF NOT EXISTS idx_edge_source
ON edge(source);

CREATE INDEX IF NOT EXISTS idx_edge_target
ON edge(target);


-- opcional para filtros por tipo
CREATE INDEX IF NOT EXISTS idx_edge_type
ON edge(type);