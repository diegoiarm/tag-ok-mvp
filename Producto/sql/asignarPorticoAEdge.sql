INSERT INTO edge_portico (edge_id, portico_id, fraction)
SELECT
    e.id,
    p.id,
    ST_LineLocatePoint(e.geometry, p.geometry) AS fraction
FROM edge e
JOIN portico p
ON ST_DWithin(e.geometry::geography, p.geometry::geography, 50);