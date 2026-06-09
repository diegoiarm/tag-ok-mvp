import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  getPorticoTarifa,
  getTramos,
  getTramoTarifa,
  updatePorticoTarifa,
  updateTramoTarifa,
} from "../api/tarifas";
import type { TarifaConfigInput } from "../types/types";

export const useTramos = () => {
  return useQuery({
    queryKey: ["tramos"],
    queryFn: getTramos,
  });
};

export const usePorticoTarifa = (id: number | null) => {
  return useQuery({
    queryKey: ["tarifas", "portico", id],
    queryFn: () => getPorticoTarifa(id as number),
    enabled: id != null,
  });
};

export const useTramoTarifa = (id: number | null) => {
  return useQuery({
    queryKey: ["tarifas", "tramo", id],
    queryFn: () => getTramoTarifa(id as number),
    enabled: id != null,
  });
};

/** Invalida las vistas afectadas por un cambio de tarifa. */
function useInvalidarTarifas() {
  const qc = useQueryClient();
  return () => {
    qc.invalidateQueries({ queryKey: ["tarifas"] });
    qc.invalidateQueries({ queryKey: ["porticos"] });
    qc.invalidateQueries({ queryKey: ["tramos"] });
  };
}

export const useUpdatePorticoTarifa = () => {
  const invalidar = useInvalidarTarifas();
  return useMutation({
    mutationFn: ({ id, input }: { id: number; input: TarifaConfigInput }) =>
      updatePorticoTarifa(id, input),
    onSuccess: invalidar,
  });
};

export const useUpdateTramoTarifa = () => {
  const invalidar = useInvalidarTarifas();
  return useMutation({
    mutationFn: ({ id, input }: { id: number; input: TarifaConfigInput }) =>
      updateTramoTarifa(id, input),
    onSuccess: invalidar,
  });
};
