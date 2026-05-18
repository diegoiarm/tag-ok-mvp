import type { Usuario, VehiculoUsuario } from "@/hooks/useUsuarios";

export interface UserKpis {
  total: number;
  activos: number;
  inactivos: number;
  conVehiculo: number;
  sinVehiculo: number;
  totalVehiculos: number;
  adopcionPct: number;
  ultimaSemana: number;
}

export function calcularKpis(usuarios: Usuario[] | undefined): UserKpis {
  if (!usuarios?.length) {
    return {
      total: 0,
      activos: 0,
      inactivos: 0,
      conVehiculo: 0,
      sinVehiculo: 0,
      totalVehiculos: 0,
      adopcionPct: 0,
      ultimaSemana: 0,
    };
  }

  const sieteDiasMs = 7 * 24 * 60 * 60 * 1000;
  const ahora = Date.now();
  let activos = 0;
  let conVehiculo = 0;
  let totalVehiculos = 0;
  let ultimaSemana = 0;

  for (const u of usuarios) {
    if (u.activo) activos++;
    if (u.vehiculos.length > 0) conVehiculo++;
    totalVehiculos += u.vehiculos.length;
    if (u.last_sign_in_at) {
      const t = new Date(u.last_sign_in_at).getTime();
      if (!Number.isNaN(t) && ahora - t <= sieteDiasMs) ultimaSemana++;
    }
  }

  const total = usuarios.length;
  return {
    total,
    activos,
    inactivos: total - activos,
    conVehiculo,
    sinVehiculo: total - conVehiculo,
    totalVehiculos,
    adopcionPct: total === 0 ? 0 : Math.round((conVehiculo / total) * 100),
    ultimaSemana,
  };
}

export interface RegistrosMes {
  mes: string;
  fecha: Date;
  nuevos: number;
  acumulado: number;
}

const MESES_CORTOS = [
  "Ene", "Feb", "Mar", "Abr", "May", "Jun",
  "Jul", "Ago", "Sep", "Oct", "Nov", "Dic",
];

function clavePorMes(d: Date): string {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}`;
}

function etiquetaMes(d: Date): string {
  return `${MESES_CORTOS[d.getMonth()]} ${String(d.getFullYear()).slice(2)}`;
}

export function registrosPorMes(usuarios: Usuario[] | undefined): RegistrosMes[] {
  if (!usuarios?.length) return [];

  const fechas = usuarios
    .map((u) => (u.created_at ? new Date(u.created_at) : null))
    .filter((d): d is Date => d !== null && !Number.isNaN(d.getTime()));

  if (fechas.length === 0) return [];

  fechas.sort((a, b) => a.getTime() - b.getTime());
  const primerMes = new Date(fechas[0].getFullYear(), fechas[0].getMonth(), 1);
  const ultimoMes = new Date();

  const counts = new Map<string, number>();
  for (const f of fechas) {
    const k = clavePorMes(f);
    counts.set(k, (counts.get(k) ?? 0) + 1);
  }

  const out: RegistrosMes[] = [];
  let acumulado = 0;
  const cursor = new Date(primerMes);
  while (cursor <= ultimoMes) {
    const key = clavePorMes(cursor);
    const nuevos = counts.get(key) ?? 0;
    acumulado += nuevos;
    out.push({
      mes: etiquetaMes(cursor),
      fecha: new Date(cursor),
      nuevos,
      acumulado,
    });
    cursor.setMonth(cursor.getMonth() + 1);
  }
  return out;
}

export interface TipoVehiculoDist {
  tipo: string;
  label: string;
  count: number;
  pct: number;
}

const TIPO_LABEL: Record<string, string> = {
  AUTO: "Automóvil",
  MOTO: "Motocicleta",
  CAMIONETA: "Camioneta",
  BUS: "Bus",
  CAMION: "Camión",
  CAMION_REMOLQUE: "Camión c/ remolque",
};

export function distribucionPorTipo(
  usuarios: Usuario[] | undefined,
): TipoVehiculoDist[] {
  const all: VehiculoUsuario[] = (usuarios ?? []).flatMap((u) => u.vehiculos);
  if (all.length === 0) return [];
  const counts = new Map<string, number>();
  for (const v of all) {
    counts.set(v.tipo_vehiculo, (counts.get(v.tipo_vehiculo) ?? 0) + 1);
  }
  const total = all.length;
  return Array.from(counts.entries())
    .map(([tipo, count]) => ({
      tipo,
      label: TIPO_LABEL[tipo] ?? tipo,
      count,
      pct: Math.round((count / total) * 100),
    }))
    .sort((a, b) => b.count - a.count);
}

export interface ActividadBucket {
  bucket: string;
  count: number;
}

export function actividadPorBucket(
  usuarios: Usuario[] | undefined,
): ActividadBucket[] {
  if (!usuarios?.length) {
    return [
      { bucket: "Hoy", count: 0 },
      { bucket: "Esta semana", count: 0 },
      { bucket: "Este mes", count: 0 },
      { bucket: "Más antiguo", count: 0 },
      { bucket: "Nunca", count: 0 },
    ];
  }
  const ahora = Date.now();
  const dia = 24 * 60 * 60 * 1000;
  let hoy = 0, semana = 0, mes = 0, antiguo = 0, nunca = 0;
  for (const u of usuarios) {
    if (!u.last_sign_in_at) {
      nunca++;
      continue;
    }
    const t = new Date(u.last_sign_in_at).getTime();
    if (Number.isNaN(t)) {
      nunca++;
      continue;
    }
    const diff = ahora - t;
    if (diff <= dia) hoy++;
    else if (diff <= 7 * dia) semana++;
    else if (diff <= 30 * dia) mes++;
    else antiguo++;
  }
  return [
    { bucket: "Hoy", count: hoy },
    { bucket: "Esta semana", count: semana },
    { bucket: "Este mes", count: mes },
    { bucket: "Más antiguo", count: antiguo },
    { bucket: "Nunca", count: nunca },
  ];
}

export interface TopUsuarioVehiculos {
  email: string;
  cantidad: number;
  patentes: string;
}

export function topUsuariosPorVehiculos(
  usuarios: Usuario[] | undefined,
  limit = 5,
): TopUsuarioVehiculos[] {
  return (usuarios ?? [])
    .filter((u) => u.vehiculos.length > 0)
    .map((u) => ({
      email: u.email ?? "—",
      cantidad: u.vehiculos.length,
      patentes: u.vehiculos.map((v) => v.patente).join(", "),
    }))
    .sort((a, b) => b.cantidad - a.cantidad)
    .slice(0, limit);
}

export function filtrarPorRango(
  usuarios: Usuario[] | undefined,
  dias: number | "all",
): Usuario[] {
  if (!usuarios) return [];
  if (dias === "all") return usuarios;
  const desde = Date.now() - dias * 24 * 60 * 60 * 1000;
  return usuarios.filter((u) => {
    if (!u.created_at) return false;
    const t = new Date(u.created_at).getTime();
    return !Number.isNaN(t) && t >= desde;
  });
}
