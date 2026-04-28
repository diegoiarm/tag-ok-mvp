UPDATE edge
SET cost = ST_Length(
    geography(geometry)
);

UPDATE edge
SET reverse_cost =
CASE
    WHEN oneway = 1 THEN -1
    ELSE cost
END;