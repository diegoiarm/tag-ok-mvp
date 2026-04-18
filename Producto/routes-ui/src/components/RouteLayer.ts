import { GeoJSON } from "react-leaflet";
import type { FeatureCollection } from "geojson";

type Props = {
  coords: [number, number][];
};

export function RouteLayer({ coords }: Props) {
  const data: FeatureCollection = {
    type: "FeatureCollection",
    features: [
      {
        type: "Feature",
        properties: {},
        geometry: {
          type: "LineString",
          coordinates: coords.map(([lat, lng]) => [lng, lat]), // conversión a [lng, lat] para Leaflet
        },
      },
    ],
  };

  return (
    <GeoJSON
      data={data}
      style={{ color: "#007bff", weight: 5 }} // 👈 objeto directo, igual que en Mapa
    />
  );
}