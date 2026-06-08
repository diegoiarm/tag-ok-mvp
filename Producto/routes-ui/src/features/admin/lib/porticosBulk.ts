import type { PorticoBulkItem } from "@/types/types";

export const CSV_HEADERS = [
  "autopistaCodigo",
  "codigo",
  "nombre",
  "sentido",
  "latitud",
  "longitud",
] as const;

/** Alias aceptados por columna para tolerar variantes de encabezado. */
const ALIASES: Record<string, string> = {
  autopistacodigo: "autopistaCodigo",
  autopista: "autopistaCodigo",
  codigoautopista: "autopistaCodigo",
  codigo: "codigo",
  nombre: "nombre",
  sentido: "sentido",
  latitud: "latitud",
  lat: "latitud",
  longitud: "longitud",
  lon: "longitud",
  lng: "longitud",
};

function normalizarClave(clave: string): string {
  const limpia = clave.trim().toLowerCase().replace(/\s+/g, "");
  return ALIASES[limpia] ?? limpia;
}

function aNumero(valor: unknown): number | null {
  if (valor === null || valor === undefined || valor === "") return null;
  const n = Number(String(valor).trim().replace(",", "."));
  return Number.isFinite(n) ? n : null;
}

function aItem(raw: Record<string, unknown>): PorticoBulkItem {
  return {
    autopistaCodigo: String(raw.autopistaCodigo ?? "").trim(),
    codigo: String(raw.codigo ?? "").trim(),
    nombre: String(raw.nombre ?? "").trim(),
    sentido: String(raw.sentido ?? "").trim(),
    latitud: aNumero(raw.latitud),
    longitud: aNumero(raw.longitud),
  };
}

/** Divide una línea CSV respetando comillas dobles. */
function dividirLinea(linea: string, delimitador: string): string[] {
  const campos: string[] = [];
  let actual = "";
  let entreComillas = false;

  for (let i = 0; i < linea.length; i++) {
    const c = linea[i];
    if (c === '"') {
      if (entreComillas && linea[i + 1] === '"') {
        actual += '"';
        i++;
      } else {
        entreComillas = !entreComillas;
      }
    } else if (c === delimitador && !entreComillas) {
      campos.push(actual);
      actual = "";
    } else {
      actual += c;
    }
  }
  campos.push(actual);
  return campos.map((c) => c.trim());
}

function parseCsv(texto: string): PorticoBulkItem[] {
  const lineas = texto
    .replace(/^\uFEFF/, "")
    .split(/\r?\n/)
    .filter((l) => l.trim() !== "");

  if (lineas.length < 2)
    throw new Error("El CSV debe tener una fila de encabezado y al menos una fila de datos.");

  const delimitador = lineas[0].includes(";") && !lineas[0].includes(",") ? ";" : ",";
  const encabezados = dividirLinea(lineas[0], delimitador).map(normalizarClave);

  return lineas.slice(1).map((linea) => {
    const valores = dividirLinea(linea, delimitador);
    const raw: Record<string, unknown> = {};
    encabezados.forEach((clave, i) => {
      raw[clave] = valores[i];
    });
    return aItem(raw);
  });
}

function parseJson(texto: string): PorticoBulkItem[] {
  const data = JSON.parse(texto);
  const arreglo = Array.isArray(data) ? data : data?.porticos;
  if (!Array.isArray(arreglo))
    throw new Error("El JSON debe ser un arreglo de pórticos o un objeto con la propiedad 'porticos'.");
  return arreglo.map((p) => aItem(p as Record<string, unknown>));
}

/** Parsea el contenido de un archivo JSON o CSV a filas de carga masiva. */
export function parsearArchivoPorticos(
  nombre: string,
  texto: string,
): PorticoBulkItem[] {
  const esJson =
    nombre.toLowerCase().endsWith(".json") || texto.trim().startsWith("[") ||
    texto.trim().startsWith("{");
  const items = esJson ? parseJson(texto) : parseCsv(texto);
  if (items.length === 0)
    throw new Error("El archivo no contiene pórticos.");
  return items;
}

/** Genera y descarga una plantilla JSON de ejemplo (arreglo de pórticos). */
export function descargarPlantillaJson() {
  const ejemplo: PorticoBulkItem[] = [
    {
      autopistaCodigo: "CN",
      codigo: "P0",
      nombre: "P. San Francisco",
      sentido: "PO",
      latitud: -33.371363,
      longitud: -70.52338,
    },
    {
      autopistaCodigo: "CN",
      codigo: "P1",
      nombre: "Gran Vía",
      sentido: "PO",
      latitud: -33.375745,
      longitud: -70.543111,
    },
  ];
  const blob = new Blob([JSON.stringify(ejemplo, null, 2)], {
    type: "application/json",
  });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "plantilla_porticos.json";
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

/** Genera y descarga una plantilla CSV de ejemplo. */
export function descargarPlantillaCsv() {
  const ejemplo = [
    CSV_HEADERS.join(","),
    "CN,P0,P. San Francisco,PO,-33.371363,-70.523380",
    "CN,P1,Gran Vía,PO,-33.375745,-70.543111",
  ].join("\n");
  const blob = new Blob(["\uFEFF" + ejemplo], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "plantilla_porticos.csv";
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}
