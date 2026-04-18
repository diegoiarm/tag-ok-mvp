import type { CalendarioTarifarioResponse, TipoTarifa, TipoDia } from "../types/types";

const tipoTarifaLabel: Record<TipoTarifa, string> = {
  TBFP: "Tarifa Base Fuera de Punta",
  TBP: "Tarifa Base Punta",
  TS: "Tarifa Saturación",
};

const tipoDiaLabel: Record<TipoDia, string> = {
  LABORAL: "Laboral",
  SABADO_FESTIVO: "Sábado / Festivo",
  DOMINGO: "Domingo",
};

type Props = {
  calendario: CalendarioTarifarioResponse | null | undefined;
};

export function CalendarioTarifario({ calendario }: Props) {
  if (!calendario || !calendario.reglas || calendario.reglas.length === 0) {
    return (
      <>
        <hr />
        <p>No hay información de calendario tarifario</p>
      </>
    );
  }

  return (
    <>
      <hr />
      <strong>Calendario:</strong>
      <ul style={{ paddingLeft: "20px", marginTop: "4px" }}>
        {calendario.reglas.map((regla, idx) => (
          <li key={idx}>
            {tipoTarifaLabel[regla.tipoTarifa] || regla.tipoTarifa} -{" "}
            {tipoDiaLabel[regla.tipoDia] || regla.tipoDia}
            {regla.tramos.length > 0 ? (
              <ul>
                {regla.tramos.map((tramo, i) => (
                  <li key={i}>
                    {tramo.horaInicio} a {tramo.horaFin}
                  </li>
                ))}
              </ul>
            ) : (
              <div style={{ marginLeft: "20px", color: "#666" }}>
                Sin tramos horarios definidos
              </div>
            )}
          </li>
        ))}
      </ul>
    </>
  );
}