import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Mapa } from "./components/Mapa";
import { Home } from "./app/pages/Home";
import { MainLayout } from "./app/layout/MainLayout";
import { AuthProvider } from "./app/context/AuthContext";
import { Login } from "./app/pages/Login";
import { UsuariosPage } from "./features/admin/pages/UsuariosPage";

function MapaPage() {
  const start = parseLatLng("-33.45635638153053, -70.71924914114052");
  const end   = parseLatLng("-33.38551650262768, -70.56779668304394");

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
            <Route path="/usuarios" element={<UsuariosPage />} />
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