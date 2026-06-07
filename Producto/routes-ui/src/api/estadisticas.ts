import { api } from "./axios";

/** KPIs de producto (routes-service): consultas de ruta y estimaciones de tarifa. */
export interface PuntoUsoMensual {
  mes: string; // "YYYY-MM"
  consultasRutas: number;
  estimaciones: number;
}

export interface EstadisticasUso {
  totalConsultasRutas: number;
  totalEstimaciones: number;
  consultasRutasUltimos30Dias: number;
  estimacionesUltimos30Dias: number;
  porMes: PuntoUsoMensual[];
}

/** Uso del historial agregado de todos los usuarios (history-service). */
export interface PuntoHistorialAnual {
  año: number;
  cruces: number;
  gasto: number;
}

export interface EstadisticasHistorial {
  totalCruces: number;
  totalGasto: number;
  usuariosConCruces: number;
  porAnio: PuntoHistorialAnual[];
}

export const getEstadisticasUso = async (): Promise<EstadisticasUso> => {
  const { data } = await api.get<EstadisticasUso>("/routes/v1/uso/estadisticas");
  return data;
};

export const getEstadisticasHistorial = async (): Promise<EstadisticasHistorial> => {
  const { data } = await api.get<EstadisticasHistorial>(
    "/history/v1/historial/admin/estadisticas",
  );
  return data;
};
