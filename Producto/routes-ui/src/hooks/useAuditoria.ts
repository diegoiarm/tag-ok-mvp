import { useQuery } from "@tanstack/react-query";
import { getAuditoria, type RegistroAuditoria } from "../api/auditoria";

export function useAuditoria() {
  return useQuery<RegistroAuditoria[]>({
    queryKey: ["auditoria"],
    queryFn: getAuditoria,
    staleTime: 30_000,
  });
}
