import { useQuery } from "@tanstack/react-query";
import { getRoute } from "../api/routes";
import { type Coord, type RouteResponse } from "../types/types";

export const useRoute = (start: Coord, end: Coord, vehiculo: string) => {
  return useQuery<RouteResponse>({
    queryKey: ["route", start, end, vehiculo],
    queryFn: () => getRoute(start, end, vehiculo),
    enabled: !!start && !!end,
  });
};