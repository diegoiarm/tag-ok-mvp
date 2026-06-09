import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { Mapa } from "./components/Mapa";
import { Home } from "./app/pages/Home";
import { MainLayout } from "./app/layout/MainLayout";
import { AuthProvider } from "./app/context/AuthContext";
import { RutaProtegida } from "./app/auth/RutaProtegida";
import { Toaster } from "./components/ui/sonner";
import { Login } from "./app/pages/Login";
import { UsuariosPage } from "./features/admin/pages/UsuariosPage";
import { CargaMasivaPage } from "./features/admin/pages/CargaMasivaPage";
import { ReportesPage } from "./features/admin/pages/ReportesPage";
import { AutopistasPage } from "./features/admin/pages/AutopistasPage";
import { PorticosPage } from "./features/admin/pages/PorticosPage";
import { TarifasPage } from "./features/admin/pages/TarifasPage";
import { AuditoriaPage } from "./features/admin/pages/AuditoriaPage";

function MapaPage() {
  const start = parseLatLng("-33.59669493677533, -70.70101359441239");
  const end   = parseLatLng("-33.4308582555084, -70.5652230425932");

  return <Mapa start={start} end={end} />;
}

function App() 
{
  return (
    <AuthProvider>
      <Toaster />
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route element={<MainLayout />}>
            {/* Vistas generales: solo requieren sesión activa. */}
            <Route element={<RutaProtegida />}>
              <Route path="/" element={<Home />} />
              <Route path="/mapa" element={<MapaPage />} />
            </Route>

            {/* Vistas administrativas: gateadas por sección según el rol. */}
            <Route element={<RutaProtegida seccion="usuarios" />}>
              <Route path="/usuarios" element={<UsuariosPage />} />
            </Route>
            <Route element={<RutaProtegida seccion="concesionarios" />}>
              <Route path="/autopistas" element={<AutopistasPage />} />
            </Route>
            <Route element={<RutaProtegida seccion="porticos" />}>
              <Route path="/porticos" element={<PorticosPage />} />
            </Route>
            <Route element={<RutaProtegida seccion="tarifas" />}>
              <Route path="/tarifas" element={<TarifasPage />} />
            </Route>
            <Route element={<RutaProtegida seccion="reportes" />}>
              <Route path="/reportes" element={<ReportesPage />} />
            </Route>
            <Route element={<RutaProtegida seccion="auditoria" />}>
              <Route path="/auditoria" element={<AuditoriaPage />} />
            </Route>
            <Route element={<RutaProtegida seccion="carga-masiva" />}>
              <Route path="/carga-masiva" element={<CargaMasivaPage />} />
            </Route>
            {/* Ruta antigua: redirige al nuevo hub de carga masiva. */}
            <Route path="/files" element={<Navigate to="/carga-masiva" replace />} />
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