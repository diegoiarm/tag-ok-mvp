import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import type { PorticoRouteResponse } from "../types/types";

import iconShadow from "leaflet/dist/images/marker-shadow.png";

const greenIcon = L.icon({
  iconUrl: "https://maps.google.com/mapfiles/ms/icons/green-dot.png",
  shadowUrl: iconShadow,
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [1, -34],
});

export function RoutePorticoMark({ portico }: { portico: PorticoRouteResponse }) {
  return (
    <Marker position={[portico.latitud, portico.longitud]} icon={greenIcon}>
      <Popup>
        <div style={{ minWidth: "220px" }}>
          <strong>{portico.nombre}</strong>
          <br />
          Código: {portico.codigo}
          <br />
          Autopista: {portico.autopista}
          <br />
          <br />

          <strong>Cruce estimado:</strong>
          <br />
          Hora:{" "}
          {new Date(portico.fechaHora).toLocaleTimeString("es-CL")}
          <br />
          Tarifa: {portico.tarifa}
          <br />
          Valor: $
          {portico.valor.toLocaleString("es-CL")}
        </div>
      </Popup>
    </Marker>
  );
}