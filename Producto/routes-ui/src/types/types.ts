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

export interface Portico {
  id: number;
  codigo: string;
  sentido: string;
  latitud: number;
  longitud: number;
  autopista: string;
}