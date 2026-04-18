import { useQuery } from "@tanstack/react-query";
import { getPorticoById } from "../api/porticos";

export const usePortico = (id: number | null) => {
  return useQuery({
    queryKey: ["portico", id],
    queryFn: () => getPorticoById(id as number),
    enabled: id !== null,
    staleTime: 1000 * 60 * 5,
  });
};
