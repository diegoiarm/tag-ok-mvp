// components/PorticoMark.tsx
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import type { TollResponse } from "../types/types";
import { getPorticoById } from "../api/porticos";
import { TarifasList } from "./TarifasList";
import { CalendarioTarifario } from "./CalendarioTarifario";

import iconShadow from "leaflet/dist/images/marker-shadow.png";
const porticoIcon = L.icon({
    iconUrl:
        "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png",
    shadowUrl: iconShadow,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
});

type Props = {
    portico: TollResponse;
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

    return (
        <Marker
            position={[portico.latitud, portico.longitud]}
            icon={porticoIcon}
            eventHandlers={{ popupopen: handlePopupOpen }}
        >
            <Popup>
                <div style={{ minWidth: "220px" }}>
                    <strong>Código: {portico.codigo}</strong>

                    <br />

                    <strong>
                        Pórtico: {portico.nombre || "No especificado"}
                    </strong>

                    {(portico.autopista || detalle?.autopista) && (
                        <>
                            <br />
                            Autopista: {portico.autopista || detalle?.autopista}
                        </>
                    )}

                    {portico.type === "PORTICO" && (
                        <>
                            <br />
                            Sentido: {portico.sentido || "No especificado"}
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
                                <>
                                    <strong>Tarifas:</strong>

                                    <TarifasList reglas={detalle.reglas} />

                                    <CalendarioTarifario
                                        calendario={detalle.calendario}
                                    />
                                </>
                            )}

                            {detalle.type === "TRAMO" && (
                                <>
                                    <strong>Tramos:</strong>

                                    {detalle.tramos.map((tramo, index) => (
                                        <div
                                            key={index}
                                            style={{
                                                borderBottom: "1px solid #ccc",
                                                marginBottom: "6px",
                                                paddingBottom: "6px",
                                            }}
                                        >
                                            <strong>
                                                {tramo.entrada}
                                                {" → "}
                                                {tramo.salida}
                                            </strong>

                                            <TarifasList
                                                reglas={tramo.reglas}
                                            />

                                            <CalendarioTarifario
                                                calendario={tramo.calendario}
                                            />
                                        </div>
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
