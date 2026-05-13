// components/CobroMark.tsx
import { Marker, Popup, Polyline } from "react-leaflet";
import L from "leaflet";
import type { Cobro } from "../types/types";

// Iconos para entrada y salida (puedes personalizar)
const entryIcon = L.icon({
    iconUrl: "https://maps.google.com/mapfiles/ms/icons/blue-dot.png",
    iconSize: [32, 32],
});
const exitIcon = L.icon({
    iconUrl: "https://maps.google.com/mapfiles/ms/icons/yellow-dot.png",
    iconSize: [32, 32],
});

// Icono estándar para pórtico individual
const porticoIcon = L.icon({
    iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
    shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
    iconSize: [25, 41],
    iconAnchor: [12, 41],
});

export function CobroMark({ cobro }: { cobro: Cobro }) 
{
    const fecha = new Date(cobro.fechaHora);

    const texto = fecha.toLocaleString("es-CL", 
    {
        //day: "2-digit",
        //month: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
    });

    if ("porticoId" in cobro) 
    {
        return (
            <Marker
                position={[cobro.latitud, cobro.longitud]}
                icon={porticoIcon}
            >
                <Popup>
                    <strong>{cobro.nombre}</strong>
                    <br />
                    {cobro.codigo} ({cobro.autopista})
                    <br />
                    Tarifa: {cobro.tarifa} — ${cobro.valor.toLocaleString("es-CL")}
                    <br />
                    Hora estmada: {texto}
                </Popup>
            </Marker>
        );
    }

    const entradaPos: [number, number] = [
        cobro.latitudEntrada,
        cobro.longitudEntrada,
    ];
    const salidaPos: [number, number] = [
        cobro.latitudSalida,
        cobro.longitudSalida,
    ];

    return (
        <>
            <Marker position={entradaPos} icon={entryIcon}>
                <Popup>
                    <strong>{cobro.nombreEntrada}</strong> (Entrada)
                    <br />
                    Tramo {cobro.autopista}
                    <br />
                    Valor del tramo: ${cobro.valor.toLocaleString("es-CL")}
                </Popup>
            </Marker>
            <Marker position={salidaPos} icon={exitIcon}>
                <Popup>
                    <strong>{cobro.nombreSalida}</strong> (Salida)
                    <br />
                    Tramo {cobro.autopista}
                    <br />
                    Valor del tramo: ${cobro.valor.toLocaleString("es-CL")}
                </Popup>
            </Marker>
            <Polyline
                positions={[entradaPos, salidaPos]}
                pathOptions={{ color: "#FF8C00", weight: 4, dashArray: "10 6" }}
            >
                <Popup>
                    Tramo {cobro.autopista}
                    <br />
                    {cobro.nombreEntrada} → {cobro.nombreSalida}
                    <br />
                    Costo total: ${cobro.valor.toLocaleString("es-CL")}
                </Popup>
            </Polyline>
        </>
    );
}
