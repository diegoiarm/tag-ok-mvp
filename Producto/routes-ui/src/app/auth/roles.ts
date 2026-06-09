import type { User } from "@supabase/supabase-js";

/**
 * Roles internos del panel administrador.
 *
 * - `super_admin`: control total del sistema (configuración, usuarios, tarifas,
 *   bitácora/auditoría, reportes y toda la operación).
 * - `admin_operacional`: mantención operativa (concesionarios, pórticos,
 *   tarifas y carga masiva). Sin acceso a usuarios, reportes ni auditoría.
 *
 * El valor vive en Supabase `app_metadata.role`. El rol legado `"admin"` se
 * trata como `super_admin` para no romper las cuentas ya existentes.
 */
export type Rol = "super_admin" | "admin_operacional";

export const ROLES: Rol[] = ["super_admin", "admin_operacional"];

export const ROL_LABEL: Record<Rol, string> = {
  super_admin: "Super Administrador",
  admin_operacional: "Administrador Operacional",
};

export const ROL_DESCRIPCION: Record<Rol, string> = {
  super_admin: "Control total del sistema.",
  admin_operacional: "Mantención operativa del sistema.",
};

/**
 * Secciones protegidas del panel. Las vistas "generales" (Inicio, Mapa) no
 * llevan sección: están disponibles para cualquier usuario con sesión.
 */
export type Seccion =
  | "usuarios"
  | "concesionarios"
  | "porticos"
  | "tarifas"
  | "reportes"
  | "auditoria"
  | "carga-masiva";

const PERMISOS: Record<Rol, Seccion[]> = {
  super_admin: [
    "usuarios",
    "concesionarios",
    "porticos",
    "tarifas",
    "reportes",
    "auditoria",
    "carga-masiva",
  ],
  admin_operacional: ["concesionarios", "porticos", "tarifas", "carga-masiva"],
};

/** Resuelve el rol del panel a partir del valor crudo de `app_metadata.role`. */
export function rolDesde(raw: string | null | undefined): Rol | null {
  if (raw === "super_admin") return "super_admin";
  if (raw === "admin_operacional") return "admin_operacional";
  if (raw === "admin") return "super_admin"; // legado
  return null;
}

/** Resuelve el rol del panel a partir del usuario de Supabase. */
export function resolverRol(user: User | null | undefined): Rol | null {
  return rolDesde(user?.app_metadata?.role);
}

/** ¿El rol puede acceder a la sección indicada? */
export function puedeAcceder(rol: Rol | null, seccion: Seccion): boolean {
  if (!rol) return false;
  return PERMISOS[rol].includes(seccion);
}

/** Solo el Super Administrador puede gestionar usuarios y roles. */
export function esSuperAdmin(rol: Rol | null): boolean {
  return rol === "super_admin";
}
