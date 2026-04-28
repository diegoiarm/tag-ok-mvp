import { Mapa } from "./components/Mapa";

function App() {
  const start = parseLatLng("-33.429386857002406, -70.69548948316935");
  const end   = parseLatLng("-33.52616939851441, -70.7082203058305");

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