import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  cambiarEstadoPortico,
  createPortico,
  crearPorticosMasivo,
  deletePortico,
  getPorticos,
  getPorticosAdmin,
  updatePortico,
} from "../api/porticos";
import type { PorticoAdmin, PorticoFormInput } from "../types/types";

export const usePorticos = () => {
  return useQuery({
    queryKey: ["porticos"],
    queryFn: getPorticos,
  });
};

export const usePorticosAdmin = () => {
  return useQuery<PorticoAdmin[]>({
    queryKey: ["porticos", "admin"],
    queryFn: getPorticosAdmin,
  });
};

/** Invalida todas las vistas de pórticos (gestión y mapa). */
function useInvalidarPorticos() {
  const qc = useQueryClient();
  return () => qc.invalidateQueries({ queryKey: ["porticos"] });
}

export const useCreatePortico = () => {
  const invalidar = useInvalidarPorticos();
  return useMutation({
    mutationFn: createPortico,
    onSuccess: invalidar,
  });
};

export const useUpdatePortico = () => {
  const invalidar = useInvalidarPorticos();
  return useMutation({
    mutationFn: ({ id, input }: { id: number; input: PorticoFormInput }) =>
      updatePortico(id, input),
    onSuccess: invalidar,
  });
};

export const useCambiarEstadoPortico = () => {
  const invalidar = useInvalidarPorticos();
  return useMutation({
    mutationFn: ({ id, activo }: { id: number; activo: boolean }) =>
      cambiarEstadoPortico(id, activo),
    onSuccess: invalidar,
  });
};

export const useDeletePortico = () => {
  const invalidar = useInvalidarPorticos();
  return useMutation({
    mutationFn: deletePortico,
    onSuccess: invalidar,
  });
};

export const useCrearPorticosMasivo = () => {
  const invalidar = useInvalidarPorticos();
  return useMutation({
    mutationFn: crearPorticosMasivo,
    onSuccess: invalidar,
  });
};
