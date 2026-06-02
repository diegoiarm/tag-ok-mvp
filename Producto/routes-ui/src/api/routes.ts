import type { RouteResponse } from "../types/types";
import { api } from "./axios";

interface RouteRequest 
{
  lon1: number;
  lat1: number;
  lon2: number;
  lat2: number;
  vehiculo: string;
}

interface Coordinates 
{
  lon: number;
  lat: number;
}

export const getRoute = async (
  start: Coordinates, 
  end: Coordinates, 
  vehiculo: string = "AUTO"): Promise<RouteResponse> => 
{
    const requestBody: RouteRequest = 
    {
      lon1: start.lon,
      lat1: start.lat,
      lon2: end.lon,
      lat2: end.lat,
      vehiculo: vehiculo
    };

    const { data } = await api.post<RouteResponse>("/routes/v1/rutas", requestBody);

    return data;
};