// components/RoutePorticoMark.tsx
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

type Props = {
  portico: PorticoRouteResponse;
};

export function RoutePorticoMark({ portico }: Props) {
  return (
    <Marker
      position={[portico.latitud, portico.longitud]}
      icon={greenIcon}
    >
      <Popup>
        <div style={{ minWidth: "220px" }}>
          <strong>{portico.nombre}</strong>
          <br />
          Código: {portico.codigo}
          <br />
          Autopista: {portico.autopista}
          <br />
          <br />
          <strong>Tarifa aplicada:</strong>
          <br />
          Tipo: {portico.tarifa}
          <br />
          Valor: ${portico.valor}
          <br />
          Hora: {new Date(portico.fechaHora).toLocaleString()}
        </div>
      </Popup>
    </Marker>
  );
}