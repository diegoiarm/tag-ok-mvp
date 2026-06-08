import type { TipoCobro } from "@/types/types";

/**
 * Plantillas vacías de concesionaria para descargar y rellenar. La estructura
 * replica el JSON que espera `POST /v1/autopistas` (ver Producto/porticos/*.json).
 *
 * Valores permitidos:
 *  - tipoCobro: "PORTICO" | "TRAMO"
 *  - aplicaA (tipo de vehículo): MOTO, AUTO, CAMIONETA, BUS, CAMION, CAMION_REMOLQUE
 *  - tipoTarifa: TBFP (base fuera de punta), TBP (base punta), TS (saturación)
 *  - tipoDia: LABORAL, SABADO_FESTIVO, DOMINGO
 *  - tramos horarios: { "inicio": "HH:mm:ss", "fin": "HH:mm:ss" }
 */

const REGLAS_TARIFA_EJEMPLO = [
  {
    aplicaA: ["AUTO", "MOTO", "CAMIONETA"],
    valores: [
      { tipoTarifa: "TBFP", valor: 0 },
      { tipoTarifa: "TBP", valor: 0 },
      { tipoTarifa: "TS", valor: 0 },
    ],
  },
  {
    aplicaA: ["BUS", "CAMION"],
    valores: [
      { tipoTarifa: "TBFP", valor: 0 },
      { tipoTarifa: "TBP", valor: 0 },
      { tipoTarifa: "TS", valor: 0 },
    ],
  },
  {
    aplicaA: ["CAMION_REMOLQUE"],
    valores: [
      { tipoTarifa: "TBFP", valor: 0 },
      { tipoTarifa: "TBP", valor: 0 },
      { tipoTarifa: "TS", valor: 0 },
    ],
  },
];

const CALENDARIO_EJEMPLO = {
  reglas: [
    {
      tipoTarifa: "TBP",
      tipoDia: "LABORAL",
      tramos: [{ inicio: "00:00:00", fin: "00:00:00" }],
    },
    {
      tipoTarifa: "TS",
      tipoDia: "LABORAL",
      tramos: [],
    },
  ],
};

const PLANTILLA_PORTICO = {
  autopista: "",
  codigo: "",
  tipoCobro: "PORTICO",
  porticos: [
    {
      codigo: "",
      sentido: "",
      nombre: "",
      latitud: 0,
      longitud: 0,
      reglas: REGLAS_TARIFA_EJEMPLO,
      calendario: CALENDARIO_EJEMPLO,
    },
  ],
};

const PLANTILLA_TRAMO = {
  autopista: "",
  codigo: "",
  tipoCobro: "TRAMO",
  // Los pórticos son los puntos geográficos; las tarifas van por tramo.
  porticos: [
    { codigo: "", nombre: "", latitud: 0, longitud: 0 },
  ],
  tramos: [
    {
      entrada: "",
      salida: "",
      distancia: 0,
      area: "",
      sentido: "",
      reglas: REGLAS_TARIFA_EJEMPLO,
      calendario: CALENDARIO_EJEMPLO,
    },
  ],
};

function descargarJson(nombre: string, contenido: unknown) {
  const blob = new Blob([JSON.stringify(contenido, null, 2)], {
    type: "application/json",
  });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = nombre;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

/** Descarga la plantilla vacía de concesionaria del tipo de cobro indicado. */
export function descargarPlantillaAutopista(tipo: TipoCobro) {
  if (tipo === "TRAMO") {
    descargarJson("plantilla-concesionaria-tramo.json", PLANTILLA_TRAMO);
  } else {
    descargarJson("plantilla-concesionaria-portico.json", PLANTILLA_PORTICO);
  }
}
