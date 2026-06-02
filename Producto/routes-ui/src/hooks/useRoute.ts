import { useQuery } from "@tanstack/react-query";
import { getRoute } from "../api/routes";
import { type RouteResponse } from "../types/types";

export const useRoute = (start: any, end: any, vehiculo: any) => {
  return useQuery<RouteResponse>({
    queryKey: ["route", start, end, vehiculo],
    queryFn: () => getRoute(start, end, vehiculo),
    enabled: !!start && !!end,
  });
};