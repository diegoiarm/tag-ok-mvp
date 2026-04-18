import type { ReglaTarifariaResponse, TipoVehiculo, TipoTarifa } from "../types/types";

const tipoVehiculoLabel: Record<TipoVehiculo, string> = {
  AUTO: "Automóvil",
  MOTO: "Motocicleta",
  CAMIONETA: "Camioneta",
  BUS: "Autobús",
  CAMION: "Camión",
  CAMION_REMOLQUE: "Camión con remolque",
};

const tipoTarifaLabel: Record<TipoTarifa, string> = {
  TBFP: "Tarifa Base Fuera de Punta",
  TBP: "Tarifa Base Punta",
  TS: "Tarifa Saturación",
};

type Props = {
  reglas: ReglaTarifariaResponse[];
};

export function TarifasList({ reglas }: Props) {
  if (reglas.length === 0) {
    return <p>No hay reglas definidas</p>;
  }

  const tieneAlgunaTarifa = reglas.some(r => r.valores.length > 0);
  if (!tieneAlgunaTarifa) {
    return <p>No hay tarifas disponibles</p>;
  }

  return (
    <>
      {reglas.map((regla, idx) => {
        // Si la regla no aplica a ningún vehículo o no tiene valores, la omitimos o mostramos mensaje
        if (regla.aplicaA.length === 0) return null;

        return (
          <div key={idx} style={{ marginBottom: "8px" }}>
            <em>
              Aplica a:{" "}
              {regla.aplicaA.map((v) => tipoVehiculoLabel[v] || v).join(", ")}
            </em>
            {regla.valores.length > 0 ? (
              <ul style={{ margin: "2px 0", paddingLeft: "20px" }}>
                {regla.valores.map((v, i) => (
                  <li key={i}>
                    {tipoTarifaLabel[v.tipoTarifa] || v.tipoTarifa}: $
                    {v.valor.toFixed(2)}
                  </li>
                ))}
              </ul>
            ) : (
              <div style={{ marginLeft: "20px", color: "#666" }}>
                Tarifas no especificadas
              </div>
            )}
          </div>
        );
      })}
    </>
  );
}