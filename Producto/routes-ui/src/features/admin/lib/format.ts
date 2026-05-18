export function formatFecha(iso: string | null | undefined): string {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "—";
  return d.toLocaleDateString("es-CL", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

export function formatFechaHora(iso: string | null | undefined): string {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "—";
  return d.toLocaleString("es-CL", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function tiempoRelativo(iso: string | null | undefined): string {
  if (!iso) return "Nunca";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "Nunca";

  const diffMs = Date.now() - d.getTime();
  const minutos = Math.round(diffMs / 60000);
  if (minutos < 1) return "Hace un momento";
  if (minutos < 60) return `Hace ${minutos} min`;
  const horas = Math.round(minutos / 60);
  if (horas < 24) return `Hace ${horas} h`;
  const dias = Math.round(horas / 24);
  if (dias < 30) return `Hace ${dias} d`;
  const meses = Math.round(dias / 30);
  if (meses < 12) return `Hace ${meses} mes${meses === 1 ? "" : "es"}`;
  const anios = Math.round(meses / 12);
  return `Hace ${anios} año${anios === 1 ? "" : "s"}`;
}

export function iniciales(email: string | null | undefined): string {
  if (!email) return "??";
  const local = email.split("@")[0] ?? "";
  const parts = local.split(/[.\-_]/).filter(Boolean);
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase();
  }
  return local.slice(0, 2).toUpperCase() || "??";
}
