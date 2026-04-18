import { useQuery } from "@tanstack/react-query";
import { getPorticos } from "../api/porticos";

export const usePorticos = () => {
  return useQuery({
    queryKey: ["porticos"],
    queryFn: getPorticos,
  });
};