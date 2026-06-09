import { Loader2 } from "lucide-react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/app/context/auth-context";
import { puedeAcceder, resolverRol, type Seccion } from "@/app/auth/roles";

interface Props {
  /**
   * Sección requerida. Si se omite, basta con tener sesión activa
   * (vistas generales como Inicio o Mapa).
   */
  seccion?: Seccion;
}

/**
 * Guardia de rutas del panel:
 * 1. Sin sesión → redirige a /login.
 * 2. Con sesión pero sin permiso para la sección → redirige a Inicio.
 */
export function RutaProtegida({ seccion }: Props) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <Loader2 className="size-6 animate-spin text-muted-foreground" />
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (seccion) {
    const rol = resolverRol(user);
    if (!puedeAcceder(rol, seccion)) {
      return <Navigate to="/" replace />;
    }
  }

  return <Outlet />;
}
