import { Mapa } from "./components/Mapa";

function App() {
  const start = parseLatLng("-33.43493553396331, -70.68721702924955");
  const end   = parseLatLng("-33.40287674453009, -70.64327623144905");

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