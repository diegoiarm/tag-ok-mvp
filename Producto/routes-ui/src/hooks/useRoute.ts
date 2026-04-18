import { useQuery } from "@tanstack/react-query";
import { getRoute } from "../api/routes";

export const useRoute = (start: any, end: any) => {
  return useQuery({
    queryKey: ["route", start, end],
    queryFn: () => getRoute(start, end),
    enabled: !!start && !!end,
  });
};