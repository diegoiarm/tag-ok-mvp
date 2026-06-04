import axios from "axios";
import type {
  AutopistaPortico,
  AutopistaResumen,
  AutopistaTramo,
  TipoCobro,
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

interface AutopistaApiResponse {
  id: number;
  nombre: string;
  codigo: string;
  tipoCobro: TipoCobro;
  porticos?: AutopistaPortico[] | null;
  tramos?: AutopistaTramo[] | null;
}

export const getAutopistas = async (): Promise<AutopistaResumen[]> => {
  const { data } = await api.get<AutopistaApiResponse[]>("/autopistas");
  return data.map((a) => ({
    id: a.id,
    nombre: a.nombre,
    codigo: a.codigo,
    tipoCobro: a.tipoCobro,
    totalPorticos: a.porticos?.length ?? 0,
    totalTramos: a.tramos?.length ?? 0,
    porticos: a.porticos ?? [],
    tramos: a.tramos ?? [],
    raw: a,
  }));
};

export interface NuevaAutopistaInput {
  nombre: string;
  codigo: string;
  tipoCobro: TipoCobro;
}

export const createAutopista = async (
  input: NuevaAutopistaInput,
): Promise<void> => {
  try {
    // Se crea la concesionaria solo con metadatos; pórticos y tramos se
    // gestionan aparte. Las listas vacías evitan el NPE del backend.
    await api.post("/autopistas", {
      autopista: input.nombre,
      codigo: input.codigo,
      tipoCobro: input.tipoCobro,
      porticos: [],
      tramos: [],
    });
  } catch (err) {
    throw new Error(mensajeError(err, "No se pudo crear la concesionaria."));
  }
};

export const deleteAutopista = async (id: number): Promise<void> => {
  await api.delete(`/autopistas/${id}`);
};
