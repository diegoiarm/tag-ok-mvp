import type { PorticoResumen, TollResponse } from "../types/types";
import { api } from "./axios";

const urlBase = "/routes/v1/porticos";

export const getPorticos = async (): Promise<PorticoResumen[]> => {
  const { data } = await api.get(urlBase);
  return data;
};

export const getPorticoById = async (id: number): Promise<TollResponse> => {
  const { data } = await api.get(`${urlBase}/${id}`);
  return data;
};