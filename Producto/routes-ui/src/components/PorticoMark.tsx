// components/PorticoMark.tsx
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import type { PorticoResumen } from "../types/types";
import { getPorticoById } from "../api/porticos";
import { TarifasList } from "./TarifasList";
import { CalendarioTarifario } from "./CalendarioTarifario";

import iconShadow from "leaflet/dist/images/marker-shadow.png";

// Pórtico con tarifa configurada → marcador rojo.
const porticoIcon = L.icon({
    iconUrl:
        "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png",
    shadowUrl: iconShadow,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
});

// Pórtico sin tarifa configurada → marcador gris y semitransparente.
const porticoSinTarifaIcon = L.icon({
    iconUrl:
        "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-grey.png",
    shadowUrl: iconShadow,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    className: "portico-sin-tarifa",
});

type Props = {
    portico: PorticoResumen;
};

export function PorticoMark({ portico }: Props) {
    const [shouldFetch, setShouldFetch] = useState(false);

    const {
        data: detalle,
        isLoading,
        error,
    } = useQuery({
        queryKey: ["portico", portico.id],
        queryFn: () => getPorticoById(portico.id),
        enabled: shouldFetch,
        staleTime: 1000 * 60 * 5,
    });

    const handlePopupOpen = () => setShouldFetch(true);

    const codigo = detalle?.codigo;
    const nombre = detalle?.nombre;
    const sentido = detalle?.type === "PORTICO" ? detalle.sentido : undefined;
    const autopista = detalle?.autopista;

    return (
        <Marker
            position={[portico.latitud, portico.longitud]}
            icon={portico.tieneTarifa ? porticoIcon : porticoSinTarifaIcon}
            eventHandlers={{ popupopen: handlePopupOpen }}
        >
            <Popup>
                <div style={{ minWidth: "220px" }}>
                    {!portico.tieneTarifa && (
                        <div
                            style={{
                                marginBottom: "6px",
                                padding: "4px 8px",
                                borderRadius: "6px",
                                background: "#f3f4f6",
                                color: "#6b7280",
                                fontSize: "0.8em",
                                fontWeight: 600,
                            }}
                        >
                            Sin tarifa configurada
                        </div>
                    )}

                    <strong>Código: {codigo ?? "—"}</strong>

                    <br />

                    <strong>
                        Pórtico: {nombre || portico.nombre || "No especificado"}
                    </strong>
                    {sentido && (
                        <>
                            <br />
                            Sentido: {sentido}
                        </>
                    )}

                    {autopista && (
                        <>
                            <br />
                            Autopista: {autopista}
                        </>
                    )}

                    {isLoading && <p>Cargando detalle...</p>}

                    {error && (
                        <p style={{ color: "red" }}>Error al cargar detalle</p>
                    )}

                    {detalle && (
                        <div
                            style={{
                                marginTop: "8px",
                                fontSize: "0.9em",
                            }}
                        >
                            <hr />

                            {detalle.type === "PORTICO" && (
                                <details style={{ marginTop: "6px" }}>
                                    <summary
                                        style={{
                                            cursor: "pointer",
                                            fontWeight: "bold",
                                        }}
                                    >
                                        Tarifas
                                    </summary>

                                    <div style={{ marginTop: "6px" }}>
                                        <TarifasList reglas={detalle.reglas} />

                                        <CalendarioTarifario
                                            calendario={detalle.calendario}
                                        />
                                    </div>
                                </details>
                            )}

                            {detalle.type === "TRAMO" && (
                                <>
                                    <strong>Tramos:</strong>

                                    {detalle.tramos.map((tramo, index) => (
                                        <details
                                            key={index}
                                            style={{
                                                borderBottom: "1px solid #ccc",
                                                marginBottom: "6px",
                                                paddingBottom: "6px",
                                            }}
                                        >
                                            <summary
                                                style={{
                                                    cursor: "pointer",
                                                    fontWeight: "bold",
                                                }}
                                            >
                                                {tramo.entrada} → {tramo.salida}
                                            </summary>

                                            <div style={{ marginTop: "6px" }}>
                                                <TarifasList
                                                    reglas={tramo.reglas}
                                                />

                                                <CalendarioTarifario
                                                    calendario={
                                                        tramo.calendario
                                                    }
                                                />
                                            </div>
                                        </details>
                                    ))}
                                </>
                            )}
                        </div>
                    )}
                </div>
            </Popup>
        </Marker>
    );
}
