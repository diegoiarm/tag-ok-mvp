import type { PorticoResponse, PorticoResumen } from "../types/types";
import { api } from "./axios";

export const getPorticos = async (): Promise<PorticoResumen[]> => {
  const { data } = await api.get("/porticos");
  return data;
};

export const getPorticoById = async (id: number): Promise<PorticoResponse> => {
  const { data } = await api.get(`/porticos/${id}`);
  return data;
};