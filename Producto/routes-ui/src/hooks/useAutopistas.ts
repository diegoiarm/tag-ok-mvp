import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  createAutopista,
  deleteAutopista,
  getAutopistas,
} from "../api/autopistas";
import type { AutopistaResumen } from "../types/types";

export const useAutopistas = () => {
  return useQuery<AutopistaResumen[]>({
    queryKey: ["autopistas"],
    queryFn: getAutopistas,
  });
};

export const useCreateAutopista = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: createAutopista,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["autopistas"] });
    },
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
