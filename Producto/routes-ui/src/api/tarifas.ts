import axios from "axios";
import type {
  TarifaConfigInput,
  TarifaConfigResponse,
  TramoAdmin,
} from "../types/types";
import { api } from "./axios";

/** Extrae el mensaje de error del backend (campo `message`) si está disponible. */
function mensajeError(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data as { message?: string } | undefined;
    if (data?.message) return data.message;
  }
  return err instanceof Error ? err.message : fallback;
}

const porticosBase = "/routes/v1/porticos";
const tramosBase = "/routes/v1/tramos";

export const getPorticoTarifa = async (
  id: number,
): Promise<TarifaConfigResponse> => {
  const { data } = await api.get<TarifaConfigResponse>(
    `${porticosBase}/${id}/tarifas`,
  );
  return data;
};

export const updatePorticoTarifa = async (
  id: number,
  input: TarifaConfigInput,
): Promise<TarifaConfigResponse> => {
  try {
    const { data } = await api.put<TarifaConfigResponse>(
      `${porticosBase}/${id}/tarifas`,
      input,
    );
    return data;
  } catch (err) {
    throw new Error(mensajeError(err, "No se pudo actualizar la tarifa del pórtico."));
  }
};

export const getTramos = async (): Promise<TramoAdmin[]> => {
  const { data } = await api.get<TramoAdmin[]>(tramosBase);
  return data;
};

export const getTramoTarifa = async (
  id: number,
): Promise<TarifaConfigResponse> => {
  const { data } = await api.get<TarifaConfigResponse>(
    `${tramosBase}/${id}/tarifas`,
  );
  return data;
};

export const updateTramoTarifa = async (
  id: number,
  input: TarifaConfigInput,
): Promise<TarifaConfigResponse> => {
  try {
    const { data } = await api.put<TarifaConfigResponse>(
      `${tramosBase}/${id}/tarifas`,
      input,
    );
    return data;
  } catch (err) {
    throw new Error(mensajeError(err, "No se pudo actualizar la tarifa del tramo."));
  }
};
