import type { Usuario } from "@/hooks/useUsuarios";

function escapeCsv(value: unknown): string {
  if (value === null || value === undefined) return "";
  const str = String(value);
  if (/[",\n\r;]/.test(str)) {
    return `"${str.replace(/"/g, '""')}"`;
  }
  return str;
}

function toCsv(headers: string[], rows: (string | number | null)[][]): string {
  const lines = [headers.join(",")];
  for (const row of rows) {
    lines.push(row.map(escapeCsv).join(","));
  }
  return "﻿" + lines.join("\n");
}

function descargar(filename: string, contenido: string) {
  const blob = new Blob([contenido], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

function fechaArchivo(): string {
  const d = new Date();
  return [
    d.getFullYear(),
    String(d.getMonth() + 1).padStart(2, "0"),
    String(d.getDate()).padStart(2, "0"),
  ].join("");
}

export function exportarUsuariosCsv(usuarios: Usuario[]) {
  const headers = [
    "id",
    "email",
    "registrado",
    "ultimo_acceso",
    "estado",
    "rol",
    "telefono",
    "num_vehiculos",
    "patentes",
  ];
  const rows = usuarios.map((u) => [
    u.id,
    u.email ?? "",
    u.created_at ?? "",
    u.last_sign_in_at ?? "",
    u.activo ? "activo" : "inactivo",
    u.app_metadata?.role ?? "user",
    u.phone ?? "",
    u.vehiculos.length,
    u.vehiculos.map((v) => v.patente).join("; "),
  ]);
  descargar(`usuarios_${fechaArchivo()}.csv`, toCsv(headers, rows));
}

export function exportarVehiculosCsv(usuarios: Usuario[]) {
  const headers = [
    "vehiculo_id",
    "usuario_email",
    "patente",
    "tipo",
    "categoria",
    "tag",
    "alias",
    "es_principal",
    "creado",
  ];
  const rows: (string | number | null)[][] = [];
  for (const u of usuarios) {
    for (const v of u.vehiculos) {
      rows.push([
        v.id,
        u.email ?? "",
        v.patente,
        v.tipo_vehiculo,
        v.categoria,
        v.numero_tag ?? "",
        v.alias ?? "",
        v.es_principal ? "true" : "false",
        v.created_at ?? "",
      ]);
    }
  }
  descargar(`vehiculos_${fechaArchivo()}.csv`, toCsv(headers, rows));
}
