import { Mapa } from "./components/Mapa";

function App() {
  const start = parseLatLng("-33.360303450688654, -70.73614973649066");
  const end   = parseLatLng("-33.38975850714581, -70.5267993850012");

  return <Mapa start={start} end={end} />;
}

function parseLatLng(text: string) {
  const [lat, lon] = text
    .split(",")
    .map(v => parseFloat(v.trim()));

  return {
    lat: Number(lat.toFixed(5)),
    lon: Number(lon.toFixed(5)),
  };
}

export default App;