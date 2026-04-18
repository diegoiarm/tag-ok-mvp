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
  geometry: string; // GeoJSON como string
}

export interface PorticoResumen {
  id: number;
  codigo: string;
  sentido: string;
  latitud: number;
  longitud: number;
  autopista?: string;
}


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
  id: number;
  codigo: string;
  sentido: string;
  latitud: number;
  longitud: number;
  reglas: ReglaTarifariaResponse[];
  calendario: CalendarioTarifarioResponse;
  autopista?: string;
};