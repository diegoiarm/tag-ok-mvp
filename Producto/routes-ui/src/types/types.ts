export interface Coord {
    lat: number;
    lon: number;
}

export interface RouteSegment {
    seq: number;
    edgeId: number;
    node: number;
    cost: number;
    aggCost: number;
    name: string;
    geometry: string;
}

export interface PorticoRouteResponse {
    nombre: string;
    codigo: string;
    autopista: string;
    codigoAutopista: string;
    longitud: number;
    latitud: number;
    tarifa: string;
    valor: number;
    fechaHora: string;
}
export interface PorticoResumen {
    id: number;
    latitud: number;
    longitud: number;
}

/** Pórtico completo para la gestión administrativa (CU20). */
export type PorticoAdmin = {
    id: number;
    codigo: string;
    nombre: string;
    sentido: string | null;
    latitud: number;
    longitud: number;
    activo: boolean;
    autopistaId: number | null;
    autopistaNombre: string | null;
    autopistaCodigo: string | null;
    fechaCreacion: string | null;
    fechaActualizacion: string | null;
};

export type PorticoFormInput = {
    codigo: string;
    nombre: string;
    sentido: string;
    latitud: number;
    longitud: number;
    autopistaId: number;
    activo?: boolean;
};

/** Fila de carga masiva de pórticos (JSON o CSV). */
export type PorticoBulkItem = {
    autopistaCodigo: string;
    codigo: string;
    nombre: string;
    sentido: string;
    latitud: number | null;
    longitud: number | null;
};

export type BulkResult = {
    creados: number;
    fallidos: number;
    errores: string[];
};

export enum TipoVehiculo {
    MOTO = "MOTO",
    AUTO = "AUTO",
    CAMIONETA = "CAMIONETA",
    BUS = "BUS",
    CAMION = "CAMION",
    CAMION_REMOLQUE = "CAMION_REMOLQUE",
}

export enum TipoTarifa {
    TBFP = "TBFP",
    TBP = "TBP",
    TS = "TS",
}

export enum TipoDia {
    LABORAL = "LABORAL",
    SABADO_FESTIVO = "SABADO_FESTIVO",
    DOMINGO = "DOMINGO",
}

export type RangoHorarioResponse = {
    horaInicio: string; // "HH:mm:ss"
    horaFin: string;
};

export type ReglaTemporalResponse = {
    tipoTarifa: TipoTarifa;
    tipoDia: TipoDia;
    tramos: RangoHorarioResponse[];
};

export type CalendarioTarifarioResponse = {
    reglas: ReglaTemporalResponse[];
};

export type ValorTarifaResponse = {
    tipoTarifa: TipoTarifa;
    valor: number;
};

export type ReglaTarifariaResponse = {
    aplicaA: TipoVehiculo[];
    valores: ValorTarifaResponse[];
};

export type PorticoResponse = {
    type: "PORTICO";

    id: number;
    codigo: string;
    nombre: string;
    sentido: string;
    latitud: number;
    longitud: number;

    reglas: ReglaTarifariaResponse[];
    calendario: CalendarioTarifarioResponse;

    autopista?: string;
};

export type TramoResponse = {
  entrada: string;
  salida: string;
  reglas: ReglaTarifariaResponse[];
  calendario: CalendarioTarifarioResponse;
};

export type PorticoTramoResponse = {
    type: "TRAMO";

    id: number;
    codigo: string;
    nombre: string;

    latitud: number;
    longitud: number;

    autopista?: string;

    tramos: TramoResponse[];
};

export type TollResponse = PorticoResponse | PorticoTramoResponse;

export type CobroPortico = {
    porticoId: number;
    nombre: string;
    codigo: string;
    autopista: string;
    latitud: number;
    longitud: number;
    tarifa: string;
    valor: number;
    fechaHora: string;
};

export type CobroTramo = {
    entradaId: number;
    salidaId: number;
    nombreEntrada: string;
    nombreSalida: string;
    autopista: string;
    latitudEntrada: number;
    longitudEntrada: number;
    latitudSalida: number;
    longitudSalida: number;
    tarifa: string;
    valor: number;
    fechaHora: string;
};

export type Cobro = CobroPortico | CobroTramo;

export type TipoCobro = "PORTICO" | "TRAMO";

export type AutopistaPortico = {
    id: number;
    codigo: string;
    nombre: string;
    sentido?: string | null;
    latitud: number;
    longitud: number;
    reglas?: ReglaTarifariaResponse[];
    calendario?: CalendarioTarifarioResponse;
};

export type AutopistaTramoExtremo = {
    id: number;
    latitud: number;
    longitud: number;
};

export type AutopistaTramo = {
    id: number;
    entrada: AutopistaTramoExtremo;
    salida: AutopistaTramoExtremo;
    distanciaKm?: number;
    reglas?: ReglaTarifariaResponse[];
    calendario?: CalendarioTarifarioResponse;
};

export type AutopistaResumen = {
    id: number;
    nombre: string;
    codigo: string;
    tipoCobro: TipoCobro;
    totalPorticos: number;
    totalTramos: number;
    porticos: AutopistaPortico[];
    tramos: AutopistaTramo[];
    /** Objeto crudo devuelto por el backend, usado para exportar la configuración. */
    raw: unknown;
};

export type RouteResponse = {
    fechaHoraInicio: string;
    fechaHoraFin: string;
    totalCost: number;
    mergedRouteGeometry: string;
    cobros: Cobro[];
};
