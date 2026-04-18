import { useEffect, useState } from "react";
import { MapContainer, TileLayer, GeoJSON } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import type { Coord } from "../types/types";
import { useRoute } from "../hooks/useRoute";
import { usePorticos } from "../hooks/usePorticos";
import { PorticoMark } from "../components/PorticoMark";

// Íconos (puedes dejarlos aquí o importarlos de un archivo aparte)
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";

const DefaultIcon = L.icon({
  iconUrl: icon,
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});
L.Marker.prototype.options.icon = DefaultIcon;

export function Mapa({ start, end }: { start: Coord; end: Coord }) {
  const { data: segments } = useRoute(start, end);
  const { data: porticos } = usePorticos();

  const [geoJsonData, setGeoJsonData] = useState<any>(null);

  useEffect(() => {
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

    // Si necesitas la función removeLoopCoords, agrégala aquí
    // const cleaned = removeLoopCoords(allCoords);

    setGeoJsonData({
      type: "FeatureCollection",
      features: [
        {
          type: "Feature",
          properties: {},
          geometry: {
            type: "LineString",
            coordinates: allCoords, // o cleaned
          },
        },
      ],
    });
  }, [segments]);

  return (
    <div style={{ height: "100vh", width: "100%" }}>
      <MapContainer
        center={[start.lat, start.lon]}
        zoom={12}
        style={{ height: "100%", width: "100%" }}
      >
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        />

        {/* Capa de ruta directa con GeoJSON */}
        {geoJsonData && (
          <GeoJSON
            data={geoJsonData}
            style={{ color: "#007bff", weight: 5 }}
          />
        )}

        {/* Capa de pórticos */}
        {porticos?.map((p) => (
          <PorticoMark key={p.id} portico={p} />
        ))}
      </MapContainer>
    </div>
  );
}