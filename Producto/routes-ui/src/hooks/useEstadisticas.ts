import { useQuery } from "@tanstack/react-query";
import {
  getEstadisticasHistorial,
  getEstadisticasUso,
  type EstadisticasHistorial,
  type EstadisticasUso,
} from "../api/estadisticas";

export function useEstadisticasUso() {
  return useQuery<EstadisticasUso>({
    queryKey: ["estadisticas", "uso"],
    queryFn: getEstadisticasUso,
    staleTime: 60_000,
  });
}

export function useEstadisticasHistorial() {
  return useQuery<EstadisticasHistorial>({
    queryKey: ["estadisticas", "historial"],
    queryFn: getEstadisticasHistorial,
    staleTime: 60_000,
  });
}
