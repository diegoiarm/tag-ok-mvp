import { useEffect, useState } from "react";
import { MapContainer, TileLayer, GeoJSON, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import type { Feature, FeatureCollection } from "geojson";
import type { Coord, RouteSegment, Portico } from "../types/types";

// Corregir íconos por defecto de Leaflet (necesario para Webpack/Vite)
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";

const DefaultIcon = L.icon({
  iconUrl: icon,
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});
L.Marker.prototype.options.icon = DefaultIcon;

// Ícono personalizado para pórticos (rojo)
const porticoIcon = L.icon({
  iconUrl: "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png",
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
});

/**
 * Elimina bucles evidentes en una lista de coordenadas.
 * Si detecta que la ruta vuelve a un punto ya visitado (dentro de una tolerancia),
 * corta el bucle para evitar dibujar trazos redundantes.
 */
function removeLoopCoords(coords: [number, number][]): [number, number][] {
  const cleaned: [number, number][] = [];
  const tolerance = 0.0001; // ~11 metros

  for (let i = 0; i < coords.length; i++) {
    const current = coords[i];
    const foundIndex = cleaned.findIndex(
      (c) =>
        Math.abs(c[0] - current[0]) < tolerance &&
        Math.abs(c[1] - current[1]) < tolerance
    );

    if (foundIndex !== -1 && i - foundIndex > 5) {
      // Es un bucle: cortamos hasta la primera aparición
      cleaned.length = foundIndex + 1;
    } else {
      cleaned.push(current);
    }
  }
  return cleaned;
}

type MapaProps = {
  start: Coord;
  end: Coord;
};

export function Mapa({ start, end }: MapaProps) {
  const [route, setRoute] = useState<FeatureCollection | null>(null);
  const [porticos, setPorticos] = useState<Portico[]>([]);

  // Cargar ruta
  useEffect(() => {
    const url = `http://localhost:8000/api/routes?lon1=${start.lon}&lat1=${start.lat}&lon2=${end.lon}&lat2=${end.lat}`;
    fetch(url)
      .then((res) => {
        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        return res.json();
      })
      .then((data: { segments: RouteSegment[] }) => {
        if (!data.segments || data.segments.length === 0) {
          console.warn("No se recibieron segmentos de ruta");
          return;
        }

        const allCoords: [number, number][] = [];
        data.segments.forEach((seg) => {
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
            console.error("Error parseando geometría:", seg.geometry, e);
          }
        });

        if (allCoords.length === 0) return;

        const cleanedCoords = removeLoopCoords(allCoords);
        const feature: Feature = {
          type: "Feature",
          properties: {},
          geometry: {
            type: "LineString",
            coordinates: cleanedCoords,
          },
        };
        setRoute({
          type: "FeatureCollection",
          features: [feature],
        });
      })
      .catch((err) => console.error("Error cargando ruta:", err));
  }, [start, end]);

  // Cargar pórticos
  useEffect(() => {
    fetch("http://localhost:8000/porticos")
      .then((res) => {
        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        return res.json();
      })
      .then((data: Portico[]) => setPorticos(data))
      .catch((err) => console.error("Error cargando pórticos:", err));
  }, []);

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

        {/* Capa de ruta */}
        {route && <GeoJSON data={route} style={{ color: "#007bff", weight: 5 }} />}

        {/* Capa de pórticos */}
        {porticos.map((p) => (
          <Marker
            key={p.id}
            position={[p.latitud, p.longitud]}
            icon={porticoIcon}
          >
            <Popup>
              <div>
                <strong>{p.codigo}</strong>
                <br />
                Autopista: {p.autopista}
                <br />
                Sentido: {p.sentido}
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
}