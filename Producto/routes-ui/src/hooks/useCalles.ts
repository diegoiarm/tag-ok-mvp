import { useQuery } from "@tanstack/react-query";
import { getAllRoads } from "../api/routes";

export const useCalle = () => {
  return useQuery({
    queryKey: ["calles"],
    queryFn: getAllRoads
  });
};