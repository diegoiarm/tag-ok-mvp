import {
  TipoDia,
  TipoTarifa,
  TipoVehiculo,
  type TarifaConfigInput,
  type TarifaConfigResponse,
} from "@/types/types";

export const TIPOS_VEHICULO: TipoVehiculo[] = [
  TipoVehiculo.MOTO,
  TipoVehiculo.AUTO,
  TipoVehiculo.CAMIONETA,
  TipoVehiculo.BUS,
  TipoVehiculo.CAMION,
  TipoVehiculo.CAMION_REMOLQUE,
];

export const TIPOS_TARIFA: TipoTarifa[] = [
  TipoTarifa.TBFP,
  TipoTarifa.TBP,
  TipoTarifa.TS,
];

export const TIPOS_DIA: TipoDia[] = [
  TipoDia.LABORAL,
  TipoDia.SABADO_FESTIVO,
  TipoDia.DOMINGO,
];

const TARIFA_LABEL: Record<TipoTarifa, string> = {
  TBFP: "Base Fuera de Punta",
  TBP: "Base Punta",
  TS: "Saturación",
};

const DIA_LABEL: Record<TipoDia, string> = {
  LABORAL: "Laboral",
  SABADO_FESTIVO: "Sábado / Festivo",
  DOMINGO: "Domingo",
};

export function tipoTarifaLabel(tipo: TipoTarifa): string {
  return TARIFA_LABEL[tipo] ?? tipo;
}

export function tipoDiaLabel(tipo: TipoDia): string {
  return DIA_LABEL[tipo] ?? tipo;
}

/** Normaliza "HH:mm:ss" o "HH:mm" a "HH:mm" para inputs <input type="time">. */
export function aHoraCorta(hora: string | null | undefined): string {
  if (!hora) return "";
  return hora.slice(0, 5);
}

/** Convierte la respuesta del backend al modelo editable del formulario. */
export function toTarifaConfigInput(
  config: TarifaConfigResponse | undefined,
): TarifaConfigInput {
  if (!config) {
    return { reglas: [], calendario: { reglas: [] } };
  }

  return {
    reglas: (config.reglas ?? []).map((r) => ({
      aplicaA: [...r.aplicaA],
      valores: (r.valores ?? []).map((v) => ({
        tipoTarifa: v.tipoTarifa,
        valor: v.valor,
      })),
    })),
    calendario: {
      reglas: (config.calendario?.reglas ?? []).map((r) => ({
        tipoTarifa: r.tipoTarifa,
        tipoDia: r.tipoDia,
        tramos: (r.tramos ?? []).map((t) => ({
          inicio: aHoraCorta(t.horaInicio),
          fin: aHoraCorta(t.horaFin),
        })),
      })),
    },
  };
}
