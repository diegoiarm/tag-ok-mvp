import { useEffect, useState } from "react";
import { MapContainer, TileLayer, GeoJSON, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import type { Coord } from "../types/types";
import { useRoute } from "../hooks/useRoute";
import { usePorticos } from "../hooks/usePorticos";
import { PorticoMark } from "../components/PorticoMark";
import { RoutePorticoMark } from "./RoutePorticoMark";

import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";

const DefaultIcon = L.icon({
  iconUrl: icon,
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});
L.Marker.prototype.options.icon = DefaultIcon;

const StartIcon = L.icon({
  iconUrl: "https://maps.google.com/mapfiles/ms/icons/green-dot.png",
  iconSize: [32, 32],
});

const EndIcon = L.icon({
  iconUrl: "https://maps.google.com/mapfiles/ms/icons/red-dot.png",
  iconSize: [32, 32],
});

export function Mapa({ start, end }: { start: Coord; end: Coord }) 
{
  const { data: route } = useRoute(start, end);
  const { data: porticos } = usePorticos();

  const [geoJsonData, setGeoJsonData] = useState<any>(null);

  const routePorticos = route?.porticos;

  useEffect(() => {
    if (!route) {
      setGeoJsonData(null);
      return;
    }

    if (route.mergedRouteGeometry) 
    {
      try {
        const merged = JSON.parse(route.mergedRouteGeometry);

        setGeoJsonData(merged);
        return;
      } catch (err) {
        console.error("Error al parsear mergedRouteGeometry", err);
      }
    }

    const segments = route.segments;
    if (!segments || segments.length === 0) {
      setGeoJsonData(null);
      return;
    }

    const allCoords: [number, number][] = [];

    segments.forEach((seg) => {
      try {
        const geom = JSON.parse(seg.geometry);
        if (geom.type === "LineString") {
          allCoords.push(...geom.coordinates);
        } else if (geom.type === "MultiLineString") {
          geom.coordinates.forEach((line: [number, number][]) => {
            allCoords.push(...line);
          });
        }
      } catch (e) {
        console.error("Error parseando geometría:", e);
      }
    });

    if (allCoords.length === 0) {
      setGeoJsonData(null);
      return;
    }

    setGeoJsonData({
      type: "FeatureCollection",
      features: [
        {
          type: "Feature",
          properties: {},
          geometry: {
            type: "LineString",
            coordinates: allCoords,
          },
        },
      ],
    });
  }, [route]);

  return (
    <div style={{ height: "100vh", width: "100%" }}>
      <MapContainer
        center={[start.lat, start.lon]}
        zoom={12}
        style={{ height: "100%", width: "100%" }}
      >
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; OpenStreetMap contributors'
        />

        {geoJsonData && (
          <GeoJSON data={geoJsonData} style={{ color: "#007bff", weight: 5 }} />
        )}

        {routePorticos?.map((p) => (
          <RoutePorticoMark key={p.codigo} portico={p} />
        ))}

        <Marker position={[start.lat, start.lon]} icon={StartIcon}>
          <Popup>
            <strong>Inicio</strong>
            <br />
            Hora:{" "}
            {route
              ? new Date(route.fechaHoraInicio).toLocaleTimeString("es-CL")
              : "Calculando..."}
          </Popup>
        </Marker>

        <Marker position={[end.lat, end.lon]} icon={EndIcon}>
          <Popup>
            <strong>Destino</strong>
            <br />
            Hora llegada:{" "}
            {route
              ? new Date(route.fechaHoraFin).toLocaleTimeString("es-CL")
              : "Calculando..."}
            <br />
            Total:{" "}
            {route
              ? `$${route.totalCost.toLocaleString("es-CL")}`
              : "..."}
          </Popup>
        </Marker>

        {porticos?.map((p) => (
          <PorticoMark key={p.id} portico={p} />
        ))}
      </MapContainer>
    </div>
  );
}