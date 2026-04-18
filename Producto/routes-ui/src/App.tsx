import { Mapa } from "./components/Mapa";

function App() {
  const start = { lat: -33.449309, lon: -70.727834 };
  const end   = { lat: -33.40302,  lon: -70.517323 };

  return <Mapa start={start} end={end} />;
}

export default App;