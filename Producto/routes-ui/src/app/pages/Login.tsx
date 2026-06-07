import { useEffect, useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import {
  Mail,
  Lock,
  Eye,
  EyeOff,
  Loader2,
  ArrowRight,
  AlertCircle,
  BarChart3,
  MapPin,
} from "lucide-react";
import { supabase } from "../lib/supabase";
import { useAuth } from "../context/AuthContext";
import logoTagOk from "@/assets/logo_tagok.svg";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";

const ERROR_MESSAGES: Record<string, string> = {
  "Invalid login credentials": "Correo o contraseña incorrectos.",
  "Email not confirmed": "Debes confirmar tu correo antes de ingresar.",
};

function traducirError(message: string): string {
  return ERROR_MESSAGES[message] ?? message;
}

export function Login() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Si ya hay sesión activa, no tiene sentido quedarse en el login.
  useEffect(() => {
    if (user) navigate("/", { replace: true });
  }, [user, navigate]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    const { error } = await supabase.auth.signInWithPassword({ email, password });
    setLoading(false);
    if (error) {
      setError(traducirError(error.message));
      return;
    }
    navigate("/", { replace: true });
  };

  return (
    <div className="grid min-h-svh w-full lg:grid-cols-[1.1fr_1fr]">
      {/* ---------------------------------------------------------------- */}
      {/* Panel de marca (solo escritorio)                                  */}
      {/* ---------------------------------------------------------------- */}
      <aside className="relative hidden overflow-hidden bg-brand text-brand-foreground lg:flex lg:flex-col">
        {/* Capas de gradiente "aurora" con deriva lenta */}
        <div className="pointer-events-none absolute inset-0">
          <div className="login-aurora absolute -left-1/4 -top-1/4 h-[70%] w-[70%] rounded-full bg-white/15 blur-3xl" />
          <div
            className="login-aurora absolute -bottom-1/4 right-0 h-[60%] w-[60%] rounded-full bg-sky-300/20 blur-3xl"
            style={{ animationDelay: "-9s" }}
          />
        </div>

        {/* Patrón de ruta animado de fondo */}
        <svg
          className="pointer-events-none absolute inset-0 h-full w-full opacity-[0.18]"
          viewBox="0 0 400 600"
          fill="none"
          preserveAspectRatio="xMidYMid slice"
          aria-hidden="true"
        >
          <path
            className="login-route"
            d="M-20 520 C 120 460, 80 340, 200 300 S 320 180, 300 60 S 360 -40, 440 -20"
            stroke="white"
            strokeWidth="3"
            strokeLinecap="round"
          />
          <circle className="login-float" cx="200" cy="300" r="7" fill="white" />
          <circle className="login-float-delayed" cx="300" cy="60" r="7" fill="white" />
        </svg>

        {/* Contenido del panel */}
        <div className="relative z-10 flex h-full flex-col justify-between p-12">
          <div className="flex items-center gap-3">
            {/* Logo en blanco sobre el azul de marca */}
            <img
              src={logoTagOk}
              alt="TAG OK"
              className="h-9 w-auto brightness-0 invert drop-shadow-sm"
            />
            <span className="border-l border-white/25 pl-3 text-sm font-medium text-white/80">
              Panel administrador
            </span>
          </div>

          <div className="max-w-md space-y-6">
            <h1 className="text-balance text-4xl font-semibold leading-tight tracking-tight">
              Gestiona tus peajes con claridad y control.
            </h1>
            <p className="text-pretty text-base leading-relaxed text-white/75">
              Autopistas, pórticos, tarifas y reportes en un solo lugar.
              Visualiza rutas y administra la red TAG OK de Santiago.
            </p>

            <ul className="space-y-3 pt-2 text-sm text-white/85">
              <FeatureItem icon={MapPin}>
                Pórticos y tarifas geolocalizados sobre el mapa
              </FeatureItem>
              <FeatureItem icon={BarChart3}>
                Reportes de cruces y recaudación al instante
              </FeatureItem>
            </ul>
          </div>

          <p className="text-xs text-white/50">
            © {new Date().getFullYear()} TAG OK · Santiago de Chile
          </p>
        </div>
      </aside>

      {/* ---------------------------------------------------------------- */}
      {/* Panel del formulario                                              */}
      {/* ---------------------------------------------------------------- */}
      <main className="flex items-center justify-center bg-background px-6 py-12">
        <div className="w-full max-w-sm duration-700 animate-in fade-in slide-in-from-bottom-4">
          {/* Logo (visible en móvil) */}
          <div className="mb-8 flex justify-center lg:hidden">
            <img src={logoTagOk} alt="TAG OK" className="h-12 w-auto" />
          </div>

          <div className="mb-8 space-y-1.5 text-center lg:text-left">
            <h2 className="text-2xl font-semibold tracking-tight">
              Bienvenido de vuelta
            </h2>
            <p className="text-sm text-muted-foreground">
              Ingresa tus credenciales para acceder al panel.
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="email">Correo electrónico</Label>
              <div className="relative">
                <Mail className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  id="email"
                  type="email"
                  autoComplete="email"
                  placeholder="tu@correo.com"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="h-10 pl-9"
                />
              </div>
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="password">Contraseña</Label>
              <div className="relative">
                <Lock className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  autoComplete="current-password"
                  placeholder="••••••••"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="h-10 px-9"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((v) => !v)}
                  aria-label={showPassword ? "Ocultar contraseña" : "Mostrar contraseña"}
                  className="absolute right-2.5 top-1/2 -translate-y-1/2 rounded-md p-1 text-muted-foreground transition-colors hover:text-foreground"
                >
                  {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                </button>
              </div>
            </div>

            {error && (
              <div className="flex items-start gap-2 rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive duration-300 animate-in fade-in slide-in-from-top-1">
                <AlertCircle className="mt-0.5 size-4 shrink-0" />
                <span>{error}</span>
              </div>
            )}

            <Button
              type="submit"
              size="lg"
              disabled={loading}
              className="group relative h-10 w-full overflow-hidden"
            >
              {/* Brillo que recorre el botón al pasar el cursor */}
              <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-full" />
              {loading ? (
                <>
                  <Loader2 className="size-4 animate-spin" />
                  Ingresando…
                </>
              ) : (
                <>
                  Iniciar sesión
                  <ArrowRight className="size-4 transition-transform duration-300 group-hover:translate-x-0.5" />
                </>
              )}
            </Button>
          </form>
        </div>
      </main>
    </div>
  );
}

function FeatureItem({
  icon: Icon,
  children,
}: {
  icon: React.ComponentType<{ className?: string }>;
  children: React.ReactNode;
}) {
  return (
    <li className="flex items-center gap-3">
      <span className="flex size-8 shrink-0 items-center justify-center rounded-lg bg-white/15 ring-1 ring-white/20">
        <Icon className="size-4" />
      </span>
      {children}
    </li>
  );
}
