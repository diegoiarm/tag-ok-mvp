import type {
  AutopistaPortico,
  AutopistaResumen,
  AutopistaTramo,
  TipoCobro,
} from "../types/types";
import { api } from "./axios";

interface AutopistaApiResponse {
  id: number;
  nombre: string;
  codigo: string;
  tipoCobro: TipoCobro;
  porticos?: AutopistaPortico[] | null;
  tramos?: AutopistaTramo[] | null;
}

const urlBase = "/routes/v1/autopistas";

export const getAutopistas = async (): Promise<AutopistaResumen[]> => {
  const { data } = await api.get<AutopistaApiResponse[]>(urlBase);
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

export const deleteAutopista = async (id: number): Promise<void> => {
  await api.delete(`${urlBase}/${id}`);
};
