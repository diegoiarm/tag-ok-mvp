import { useQuery } from "@tanstack/react-query";
import { supabase } from "../app/lib/supabase";

export interface Usuario {
  id: string;
  email: string;
  created_at: string;
  last_sign_in_at: string | null;
  phone: string | null;
}

async function fetchUsuarios(): Promise<Usuario[]> {
  const { data: { session } } = await supabase.auth.getSession();
  if (!session) throw new Error("No autenticado");

  const res = await fetch(
    "https://ibafvqmoqeabmziyzifk.supabase.co/functions/v1/list-users",
    {
      headers: {
        Authorization: `Bearer ${session.access_token}`,
      },
    }
  );

  if (!res.ok) throw new Error("Error al obtener usuarios");
  return res.json();
}

export function useUsuarios() {
  return useQuery<Usuario[]>({
    queryKey: ["usuarios"],
    queryFn: fetchUsuarios,
  });
}
