import * as XLSX from "xlsx";
import type { Usuario } from "@/hooks/useUsuarios";
import type { AutopistaResumen, PorticoAdmin } from "@/types/types";
import type {
  EstadisticasHistorial,
  EstadisticasUso,
} from "@/api/estadisticas";
import type {
  KpisOperativos,
  RegistrosMes,
  TipoVehiculoDist,
  UserKpis,
} from "./analytics";

export interface ReporteData {
  rango: string;
  usuarios: Usuario[];
  kpis: UserKpis;
  registros: RegistrosMes[];
  distribucion: TipoVehiculoDist[];
  kpisOp: KpisOperativos;
  porticos: PorticoAdmin[];
  autopistas: AutopistaResumen[];
  uso?: EstadisticasUso;
  historial?: EstadisticasHistorial;
}

const RANGO_LABEL: Record<string, string> = {
  "7": "Últimos 7 días",
  "30": "Últimos 30 días",
  "90": "Últimos 90 días",
  all: "Todo el período",
};

function fechaArchivo(): string {
  const d = new Date();
  return [
    d.getFullYear(),
    String(d.getMonth() + 1).padStart(2, "0"),
    String(d.getDate()).padStart(2, "0"),
  ].join("");
}

/** Aplica un ancho de columna razonable según el contenido. */
function autoWidth(rows: (string | number | null)[][]): XLSX.ColInfo[] {
  if (rows.length === 0) return [];
  const cols = rows[0].length;
  const widths: number[] = new Array(cols).fill(10);
  for (const row of rows) {
    row.forEach((cell, i) => {
      const len = cell === null || cell === undefined ? 0 : String(cell).length;
      if (len + 2 > widths[i]) widths[i] = Math.min(len + 2, 60);
    });
  }
  return widths.map((w) => ({ wch: w }));
}

function sheetFromRows(rows: (string | number | null)[][]): XLSX.WorkSheet {
  const ws = XLSX.utils.aoa_to_sheet(rows);
  ws["!cols"] = autoWidth(rows);
  return ws;
}

function hojaResumen(data: ReporteData): XLSX.WorkSheet {
  const { kpis, kpisOp, uso, historial } = data;
  const rows: (string | number | null)[][] = [
    ["Reporte de uso — TAG OK"],
    ["Generado", new Date().toLocaleString("es-CL")],
    ["Rango", RANGO_LABEL[data.rango] ?? data.rango],
    [],
    ["Sección", "Métrica", "Valor"],
    ["Adopción", "Usuarios registrados", kpis.total],
    ["Adopción", "Usuarios activos", kpis.activos],
    ["Adopción", "Usuarios inactivos", kpis.inactivos],
    ["Adopción", "Usuarios con vehículo", kpis.conVehiculo],
    ["Adopción", "% de adopción", `${kpis.adopcionPct}%`],
    ["Adopción", "Vehículos totales", kpis.totalVehiculos],
    ["Adopción", "Activos última semana", kpis.ultimaSemana],
    [],
    ["Producto", "Rutas consultadas", uso?.totalConsultasRutas ?? "N/D"],
    ["Producto", "Rutas (últimos 30 días)", uso?.consultasRutasUltimos30Dias ?? "N/D"],
    ["Producto", "Estimaciones de tarifa", uso?.totalEstimaciones ?? "N/D"],
    ["Producto", "Estimaciones (últimos 30 días)", uso?.estimacionesUltimos30Dias ?? "N/D"],
    ["Producto", "Cruces registrados", historial?.totalCruces ?? "N/D"],
    ["Producto", "Usuarios con cruces", historial?.usuariosConCruces ?? "N/D"],
    ["Producto", "Gasto en peajes (CLP)", historial?.totalGasto ?? "N/D"],
    [],
    ["Operativo", "Pórticos totales", kpisOp.totalPorticos],
    ["Operativo", "Pórticos activos", kpisOp.porticosActivos],
    ["Operativo", "Pórticos inactivos", kpisOp.porticosInactivos],
    ["Operativo", "Pórticos con tarifa", kpisOp.porticosConTarifa],
    ["Operativo", "Pórticos sin tarifa", kpisOp.porticosSinTarifa],
    ["Operativo", "Concesionarias", kpisOp.totalConcesionarias],
    ["Operativo", "Cambios últimos 7 días", kpisOp.cambiosUltimos7Dias],
    ["Operativo", "Cambios últimos 30 días", kpisOp.cambiosUltimos30Dias],
    ["Operativo", "Última actualización", kpisOp.ultimaActualizacion ?? "—"],
  ];
  return sheetFromRows(rows);
}

function hojaUsuarios(usuarios: Usuario[]): XLSX.WorkSheet {
  const rows: (string | number | null)[][] = [
    ["ID", "Email", "Registrado", "Último acceso", "Estado", "Rol", "Teléfono", "N° vehículos", "Patentes"],
    ...usuarios.map((u) => [
      u.id,
      u.email ?? "",
      u.created_at ?? "",
      u.last_sign_in_at ?? "",
      u.activo ? "activo" : "inactivo",
      u.app_metadata?.role ?? "user",
      u.phone ?? "",
      u.vehiculos.length,
      u.vehiculos.map((v) => v.patente).join("; "),
    ]),
  ];
  return sheetFromRows(rows);
}

function hojaVehiculos(usuarios: Usuario[]): XLSX.WorkSheet {
  const rows: (string | number | null)[][] = [
    ["ID", "Usuario", "Patente", "Tipo", "Categoría", "TAG", "Alias", "Principal", "Creado"],
  ];
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
        v.es_principal ? "sí" : "no",
        v.created_at ?? "",
      ]);
    }
  }
  return sheetFromRows(rows);
}

function hojaPorticos(porticos: PorticoAdmin[]): XLSX.WorkSheet {
  const rows: (string | number | null)[][] = [
    ["ID", "Código", "Nombre", "Concesionaria", "Sentido", "Estado", "Tarifa", "Creado", "Actualizado"],
    ...porticos.map((p) => [
      p.id,
      p.codigo,
      p.nombre,
      p.autopistaNombre ?? "",
      p.sentido ?? "",
      p.activo ? "activo" : "inactivo",
      p.tieneTarifa ? "configurada" : "pendiente",
      p.fechaCreacion ?? "",
      p.fechaActualizacion ?? "",
    ]),
  ];
  return sheetFromRows(rows);
}

function hojaConcesionarias(autopistas: AutopistaResumen[]): XLSX.WorkSheet {
  const rows: (string | number | null)[][] = [
    ["ID", "Código", "Nombre", "Tipo de cobro", "N° pórticos", "N° tramos"],
    ...autopistas.map((a) => [
      a.id,
      a.codigo,
      a.nombre,
      a.tipoCobro,
      a.totalPorticos,
      a.totalTramos,
    ]),
  ];
  return sheetFromRows(rows);
}

function hojaUsoMensual(data: ReporteData): XLSX.WorkSheet {
  const rows: (string | number | null)[][] = [["Mes", "Rutas consultadas", "Estimaciones de tarifa"]];
  for (const p of data.uso?.porMes ?? []) {
    rows.push([p.mes, p.consultasRutas, p.estimaciones]);
  }
  rows.push([]);
  rows.push(["Año", "Cruces", "Gasto (CLP)"]);
  for (const p of data.historial?.porAnio ?? []) {
    rows.push([p.año, p.cruces, p.gasto]);
  }
  return sheetFromRows(rows);
}

/** Genera y descarga un workbook .xlsx consolidado con todos los KPIs y datasets. */
export function exportarReporteExcel(data: ReporteData) {
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, hojaResumen(data), "Resumen KPIs");
  XLSX.utils.book_append_sheet(wb, hojaUsuarios(data.usuarios), "Usuarios");
  XLSX.utils.book_append_sheet(wb, hojaVehiculos(data.usuarios), "Vehículos");
  XLSX.utils.book_append_sheet(wb, hojaPorticos(data.porticos), "Pórticos");
  XLSX.utils.book_append_sheet(wb, hojaConcesionarias(data.autopistas), "Concesionarias");
  XLSX.utils.book_append_sheet(wb, hojaUsoMensual(data), "Uso de producto");
  XLSX.writeFile(wb, `reporte_tagok_${fechaArchivo()}.xlsx`);
}
