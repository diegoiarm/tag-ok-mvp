import { createContext, useContext } from "react";
import type { User } from "@supabase/supabase-js";

export type AuthContextValue = {
  user: User | null;
  /** `true` mientras Supabase resuelve la sesión inicial. */
  loading: boolean;
};

export const AuthContext = createContext<AuthContextValue | null>(null);

export const useAuth = (): AuthContextValue => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth debe usarse dentro de <AuthProvider>");
  }
  return ctx;
};
