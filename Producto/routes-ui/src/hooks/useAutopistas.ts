import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { deleteAutopista, getAutopistas } from "../api/autopistas";
import type { AutopistaResumen } from "../types/types";

export const useAutopistas = () => {
  return useQuery<AutopistaResumen[]>({
    queryKey: ["autopistas"],
    queryFn: getAutopistas,
  });
};

export const useDeleteAutopista = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: deleteAutopista,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["autopistas"] });
      qc.invalidateQueries({ queryKey: ["porticos"] });
    },
  });
};
