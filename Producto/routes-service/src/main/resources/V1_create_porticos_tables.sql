CREATE TABLE autopista (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    codigo VARCHAR(255)
);

CREATE TABLE portico (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL,
    nombre VARCHAR(255),
    sentido VARCHAR(255),
    latitud DOUBLE PRECISION,
    longitud DOUBLE PRECISION,
    autopista_id BIGINT,
    
    CONSTRAINT fk_portico_autopista
        FOREIGN KEY (autopista_id)
        REFERENCES autopista(id)
        ON DELETE SET NULL
);

CREATE TABLE regla_tarifaria (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    portico_id BIGINT,

    CONSTRAINT fk_regla_tarifaria_portico
        FOREIGN KEY (portico_id)
        REFERENCES portico(id)
        ON DELETE SET NULL
);

CREATE TABLE calendario_tarifario (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    portico_id BIGINT,

    CONSTRAINT fk_calendario_tarifario_portico
        FOREIGN KEY (portico_id)
        REFERENCES portico(id)
        ON DELETE SET NULL
);