INSERT INTO autopista (nombre)
VALUES ('Vespucio Norte')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO portico (codigo, geometry)
VALUES
('P01', ST_SetSRID(ST_MakePoint(-70.753079, -33.483395), 4326)),
('P16', ST_SetSRID(ST_MakePoint(-70.772963, -33.451188), 4326)),
('P03', ST_SetSRID(ST_MakePoint(-70.783126, -33.438686), 4326)),
('P05', ST_SetSRID(ST_MakePoint(-70.775594, -33.399589), 4326)),
('P07', ST_SetSRID(ST_MakePoint(-70.753948, -33.381316), 4326)),
('P09', ST_SetSRID(ST_MakePoint(-70.704297, -33.368836), 4326)),
('P10', ST_SetSRID(ST_MakePoint(-70.696869, -33.365942), 4326)),
('P12', ST_SetSRID(ST_MakePoint(-70.664951, -33.373406), 4326)),
('P14', ST_SetSRID(ST_MakePoint(-70.63333,  -33.388591), 4326))
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO tipo_tarifa (codigo)
VALUES ('TBFP'), ('TBP')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO tipo_vehiculo (categoria, tipo)
VALUES 
('1 y 4', 'automóvil'),
('2', 'camión'),
('3', 'bus')
ON CONFLICT (categoria) DO NOTHING;

INSERT INTO tarifa_portico (portico_id, tipo_tarifa_id, tipo_vehiculo_id, precio)
SELECT
    p.id,
    tt.id,
    tv.id,
    v.precio
FROM (VALUES
    ('P01','TBFP','1 y 4',120.59),
    ('P01','TBFP','2',241.17),
    ('P01','TBFP','3',361.76)
) AS v(portico, tarifa, vehiculo, precio)
JOIN portico p ON p.codigo = v.portico
JOIN tipo_tarifa tt ON tt.codigo = v.tarifa
JOIN tipo_vehiculo tv ON tv.categoria = v.vehiculo;

INSERT INTO tarifa_portico (portico_id, tipo_tarifa_id, tipo_vehiculo_id, precio)
SELECT
    p.id,
    tt.id,
    tv.id,
    v.precio
FROM (VALUES
    ('P01','TBP','1 y 4',241.17),
    ('P01','TBP','2',482.35),
    ('P01','TBP','3',723.52)
) AS v(portico, tarifa, vehiculo, precio)
JOIN portico p ON p.codigo = v.portico
JOIN tipo_tarifa tt ON tt.codigo = v.tarifa
JOIN tipo_vehiculo tv ON tv.categoria = v.vehiculo;

INSERT INTO horario_tarifa (tipo_tarifa_id, dia, hora_inicio, hora_fin)
SELECT
    tt.id,
    v.dia,
    v.inicio::time,
    v.fin::time
FROM (VALUES
    ('TBP','Laboral','06:30','07:00'),
    ('TBP','Laboral','10:00','12:30')
) AS v(tarifa, dia, inicio, fin)
JOIN tipo_tarifa tt ON tt.codigo = v.tarifa;