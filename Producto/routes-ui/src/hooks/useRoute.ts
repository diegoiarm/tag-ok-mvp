import { useQuery } from "@tanstack/react-query";
import { getRoute } from "../api/routes";
import { type RouteResponse } from "../types/types";

export const useRoute = (start: any, end: any) => {
  return useQuery<RouteResponse>({
    queryKey: ["route", start, end],
    queryFn: () => getRoute(start, end),
    enabled: !!start && !!end,
  });
};