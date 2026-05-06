import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Mapa } from "./components/Mapa";
import { Home } from "./app/pages/Home";
import { MainLayout } from "./app/layout/MainLayout";
import { AuthProvider } from "./app/context/AuthContext";
import { Login } from "./app/pages/Login";

function MapaPage() {
  const start = parseLatLng("-33.44384105405843, -70.80068110142963");
  const end   = parseLatLng("-33.526647849467594, -70.60596777473236");

  return <Mapa start={start} end={end} />;
}

function App() 
{
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<MainLayout />}>
            <Route path="/" element={<Home />} />
            <Route path="/mapa" element={<MapaPage />} />
            <Route path="/login" element={<Login />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
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