import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { supabase } from "../app/lib/supabase";

const SUPABASE_FUNCTIONS_URL =
  "https://ibafvqmoqeabmziyzifk.supabase.co/functions/v1";

export interface VehiculoUsuario {
  id: string;
  user_id: string;
  patente: string;
  tipo_vehiculo: string;
  categoria: number;
  numero_tag: string | null;
  alias: string | null;
  es_principal: boolean;
  created_at: string | null;
}

export interface Usuario {
  id: string;
  email: string;
  created_at: string;
  last_sign_in_at: string | null;
  phone: string | null;
  activo: boolean;
  banned_until: string | null;
  vehiculos: VehiculoUsuario[];
  app_metadata?: { role?: string };
}

async function fetchUsuarios(): Promise<Usuario[]> {
  const { data: { session } } = await supabase.auth.getSession();
  if (!session) throw new Error("No autenticado");

  const res = await fetch(`${SUPABASE_FUNCTIONS_URL}/list-users`, {
    headers: { Authorization: `Bearer ${session.access_token}` },
  });

  if (!res.ok) {
    const msg = await res.text().catch(() => "Error al obtener usuarios");
    throw new Error(msg || "Error al obtener usuarios");
  }

  const raw = (await res.json()) as Partial<Usuario>[];
  const now = Date.now();
  return raw.map((u): Usuario => {
    const banned = u.banned_until ? new Date(u.banned_until).getTime() : null;
    const activo = u.activo ?? (!banned || banned <= now);
    return {
      id: u.id ?? "",
      email: u.email ?? "",
      created_at: u.created_at ?? "",
      last_sign_in_at: u.last_sign_in_at ?? null,
      phone: u.phone ?? null,
      activo,
      banned_until: u.banned_until ?? null,
      vehiculos: u.vehiculos ?? [],
      app_metadata: u.app_metadata,
    };
  });
}

export function useUsuarios() {
  return useQuery<Usuario[]>({
    queryKey: ["usuarios"],
    queryFn: fetchUsuarios,
    staleTime: 30_000,
  });
}

interface UpdateStatusInput {
  userId: string;
  activo: boolean;
}

async function updateUserStatus(input: UpdateStatusInput): Promise<void> {
  const { data: { session } } = await supabase.auth.getSession();
  if (!session) throw new Error("No autenticado");

  const res = await fetch(`${SUPABASE_FUNCTIONS_URL}/update-user-status`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${session.access_token}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(input),
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    try {
      const parsed = JSON.parse(text);
      throw new Error(parsed.error ?? "Error al actualizar estado");
    } catch {
      throw new Error(text || "Error al actualizar estado");
    }
  }
}

export function useUpdateUserStatus() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: updateUserStatus,
    onMutate: async ({ userId, activo }) => {
      await qc.cancelQueries({ queryKey: ["usuarios"] });
      const prev = qc.getQueryData<Usuario[]>(["usuarios"]);
      qc.setQueryData<Usuario[]>(["usuarios"], (old) =>
        old?.map((u) => (u.id === userId ? { ...u, activo } : u)),
      );
      return { prev };
    },
    onError: (_err, _vars, ctx) => {
      if (ctx?.prev) qc.setQueryData(["usuarios"], ctx.prev);
    },
    onSettled: () => {
      qc.invalidateQueries({ queryKey: ["usuarios"] });
    },
  });
}
