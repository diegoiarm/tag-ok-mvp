import type { RouteSegment } from "../types/types";
import { api } from "./axios";

export const getRoute = async (start: any, end: any) => {
  const { data } = await api.get("/api/routes", {
    params: {
      lon1: start.lon,
      lat1: start.lat,
      lon2: end.lon,
      lat2: end.lat,
    },
  });

  return data.segments as RouteSegment[];
};