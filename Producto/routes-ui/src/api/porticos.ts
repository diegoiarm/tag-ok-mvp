import axios from "axios";
import type {
  BulkResult,
  PorticoAdmin,
  PorticoBulkItem,
  PorticoFormInput,
  PorticoResumen,
  TollResponse,
} from "../types/types";
import { api } from "./axios";

const urlBase = "/routes/v1/porticos";

/** Extrae el mensaje de error del backend (campo `message`) si está disponible. */
function mensajeError(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data as { message?: string } | undefined;
    if (data?.message) return data.message;
  }
  return err instanceof Error ? err.message : fallback;
}

export const getPorticos = async (): Promise<PorticoResumen[]> => {
  const { data } = await api.get(urlBase);
  return data;
};

export const getPorticoById = async (id: number): Promise<TollResponse> => {
  const { data } = await api.get(`${urlBase}/${id}`);
  return data;
};

export const getPorticosAdmin = async (): Promise<PorticoAdmin[]> => {
  const { data } = await api.get<PorticoAdmin[]>(`${urlBase}/admin`);
  return data;
};

export const createPortico = async (
  input: PorticoFormInput,
): Promise<PorticoAdmin> => {
  try {
    const { data } = await api.post<PorticoAdmin>(urlBase, {
      codigo: input.codigo,
      nombre: input.nombre,
      sentido: input.sentido,
      latitud: input.latitud,
      longitud: input.longitud,
      autopistaId: input.autopistaId,
    });
    return data;
  } catch (err) {
    throw new Error(mensajeError(err, "No se pudo crear el pórtico."));
  }
};

export const updatePortico = async (
  id: number,
  input: PorticoFormInput,
): Promise<PorticoAdmin> => {
  try {
    const { data } = await api.put<PorticoAdmin>(`${urlBase}/${id}`, {
      codigo: input.codigo,
      nombre: input.nombre,
      sentido: input.sentido,
      latitud: input.latitud,
      longitud: input.longitud,
      autopistaId: input.autopistaId,
      activo: input.activo,
    });
    return data;
  } catch (err) {
    throw new Error(mensajeError(err, "No se pudo actualizar el pórtico."));
  }
};

export const cambiarEstadoPortico = async (
  id: number,
  activo: boolean,
): Promise<PorticoAdmin> => {
  try {
    const { data } = await api.patch<PorticoAdmin>(`${urlBase}/${id}/estado`, {
      activo,
    });
    return data;
  } catch (err) {
    throw new Error(mensajeError(err, "No se pudo cambiar el estado del pórtico."));
  }
};

export const deletePortico = async (id: number): Promise<void> => {
  try {
    await api.delete(`${urlBase}/${id}`);
  } catch (err) {
    throw new Error(mensajeError(err, "No se pudo eliminar el pórtico."));
  }
};

export const crearPorticosMasivo = async (
  items: PorticoBulkItem[],
): Promise<BulkResult> => {
  try {
    const { data } = await api.post<BulkResult>(`${urlBase}/bulk`, items);
    return data;
  } catch (err) {
    throw new Error(mensajeError(err, "No se pudo procesar la carga masiva."));
  }
};
