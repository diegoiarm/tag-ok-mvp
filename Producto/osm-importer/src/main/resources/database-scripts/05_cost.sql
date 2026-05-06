UPDATE edge
SET cost = 
  (ST_Length(geometry::geography) / (s.speed_kmh * 1000.0 / 3600.0)) *
  CASE
    WHEN edge.type IN ('motorway', 'motorway_link') THEN 0.5
    WHEN edge.type IN ('trunk', 'trunk_link') THEN 0.7
    ELSE 2.5
  END
FROM (
  VALUES
    ('motorway', 100),
    ('motorway_link', 60),
    ('trunk', 80),
    ('trunk_link', 50),
    ('primary', 60),
    ('primary_link', 40),
    ('secondary', 50),
    ('secondary_link', 30),
    ('tertiary', 40),
    ('tertiary_link', 20),
    ('residential', 30),
    ('service', 20),
    ('unclassified', 30)
) AS s(type, speed_kmh)
WHERE edge.type = s.type;

UPDATE edge
SET reverse_cost =
CASE
    WHEN oneway = 1 THEN -1
    ELSE cost
END;