import { useMemo, useRef, useState } from "react";
import { Link } from "react-router-dom";
import {
  Bar,
  BarChart,
  CartesianGrid,
  XAxis,
  YAxis,
} from "recharts";
import {
  Activity,
  ArrowRight,
  Building2,
  Car,
  MapPin,
  Moon,
  Plus,
  RefreshCw,
  Route as RouteIcon,
  Sun,
  Sunrise,
  Tag,
  TrendingUp,
  Upload,
  Users,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
  type ChartConfig,
} from "@/components/ui/chart";
import { Skeleton } from "@/components/ui/skeleton";
import { useAutopistas } from "@/hooks/useAutopistas";
import { usePorticosAdmin } from "@/hooks/usePorticos";
import { useUsuarios } from "@/hooks/useUsuarios";
import {
  useEstadisticasHistorial,
  useEstadisticasUso,
} from "@/hooks/useEstadisticas";
import { useAuditoria } from "@/hooks/useAuditoria";
import { calcularKpis, kpisOperativos } from "@/features/admin/lib/analytics";
import { formatCLP, iniciales, tiempoRelativo } from "@/features/admin/lib/format";
import { useAuth } from "@/app/context/auth-context";
import type { TipoAccion } from "@/api/auditoria";

const MESES_CORTOS = [
  "Ene", "Feb", "Mar", "Abr", "May", "Jun",
  "Jul", "Ago", "Sep", "Oct", "Nov", "Dic",
];

function etiquetaMesIso(iso: string): string {
  const [anio, mes] = iso.split("-");
  const idx = Number(mes) - 1;
  if (idx < 0 || idx > 11) return iso;
  return `${MESES_CORTOS[idx]} ${anio.slice(2)}`;
}

export function Home() {
  const { user } = useAuth();
  const videoRef = useRef<HTMLVideoElement>(null);
  const [mostrarHover, setMostrarHover] = useState(false);

  const autopistasQ = useAutopistas();
  const porticosQ = usePorticosAdmin();
  const usuariosQ = useUsuarios();
  const usoQ = useEstadisticasUso();
  const historialQ = useEstadisticasHistorial();
  const auditoriaQ = useAuditoria();

  const kpisUsuarios = useMemo(
    () => calcularKpis(usuariosQ.data),
    [usuariosQ.data],
  );
  const kpisOp = useMemo(
    () => kpisOperativos(porticosQ.data, autopistasQ.data),
    [porticosQ.data, autopistasQ.data],
  );
  const usoMensual = useMemo(
    () =>
      (usoQ.data?.porMes ?? []).map((p) => ({
        mes: etiquetaMesIso(p.mes),
        consultasRutas: p.consultasRutas,
        estimaciones: p.estimaciones,
      })),
    [usoQ.data],
  );
  const actividadReciente = useMemo(
    () => (auditoriaQ.data ?? []).slice(0, 6),
    [auditoriaQ.data],
  );

  const refrescando =
    autopistasQ.isFetching ||
    porticosQ.isFetching ||
    usuariosQ.isFetching ||
    usoQ.isFetching ||
    historialQ.isFetching;

  const refrescarTodo = () => {
    autopistasQ.refetch();
    porticosQ.refetch();
    usuariosQ.refetch();
    usoQ.refetch();
    historialQ.refetch();
    auditoriaQ.refetch();
  };

  const saludo = obtenerSaludo();
  const nombreUsuario = user?.email?.split("@")[0] ?? "administrador";

  const reproducirVideo = () => {
    videoRef.current?.play();
    setMostrarHover(true);
  };

  return (
    <div>
      {/* Botón invisible (easter egg) — no lo borren pls */}
      <button
        onClick={reproducirVideo}
        aria-hidden
        tabIndex={-1}
        style={{
          position: "fixed",
          top: 0,
          left: 0,
          width: "40px",
          height: "40px",
          opacity: 0,
          border: "none",
          background: "transparent",
          cursor: "default",
          zIndex: 9999,
        }}
      />
      {mostrarHover && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            background: "rgba(0,0,0,0.85)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 10000,
          }}
        >
          <video
            ref={videoRef}
            src="/ssstik.io_@minionfan532_1777578739485.mp4"
            autoPlay
            onEnded={() => setMostrarHover(false)}
            style={{ maxWidth: "90%", maxHeight: "90%", borderRadius: "12px" }}
          />
        </div>
      )}

      <div className="mx-auto max-w-7xl px-6 py-8 space-y-8">
        <header className="flex items-start justify-between gap-4 flex-wrap animate-in fade-in slide-in-from-bottom-2 duration-500">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight flex items-center gap-2">
              <saludo.icon
                className={`h-6 w-6 shrink-0 ${saludo.color} animate-in zoom-in-50 duration-700`}
              />
              <span>
                {saludo.texto}, <span className="capitalize">{nombreUsuario}</span>
              </span>
            </h1>
            <p className="text-sm text-muted-foreground mt-1">
              Resumen general del sistema de peajes TAG.
            </p>
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={refrescarTodo}
            disabled={refrescando}
          >
            <RefreshCw className={`h-4 w-4 ${refrescando ? "animate-spin" : ""}`} />
            Actualizar
          </Button>
        </header>

        {/* KPIs principales (estado del sistema) */}
        <section className="grid grid-cols-2 lg:grid-cols-4 gap-3 animate-in fade-in slide-in-from-bottom-2 duration-500 delay-75 fill-mode-both">
          <KpiCard
            label="Concesionarias"
            value={kpisOp.totalConcesionarias}
            icon={Building2}
            to="/autopistas"
            loading={autopistasQ.isLoading}
            hint="autopistas registradas"
          />
          <KpiCard
            label="Pórticos"
            value={kpisOp.totalPorticos}
            icon={MapPin}
            to="/porticos"
            loading={porticosQ.isLoading}
            hint={`${kpisOp.porticosActivos} activos · ${kpisOp.porticosSinTarifa} sin tarifa`}
          />
          <KpiCard
            label="Usuarios"
            value={kpisUsuarios.total}
            icon={Users}
            to="/usuarios"
            loading={usuariosQ.isLoading}
            unavailable={usuariosQ.isError}
            hint={`${kpisUsuarios.activos} activos · ${kpisUsuarios.adopcionPct}% con vehículo`}
          />
          <KpiCard
            label="Gasto en peajes"
            valueText={formatCLP(historialQ.data?.totalGasto)}
            icon={TrendingUp}
            accent="text-brand"
            to="/reportes"
            loading={historialQ.isLoading}
            unavailable={historialQ.isError}
            hint={
              historialQ.data
                ? `${historialQ.data.totalCruces.toLocaleString("es-CL")} cruces registrados`
                : undefined
            }
          />
        </section>

        {/* Uso del producto */}
        <section className="space-y-3 animate-in fade-in slide-in-from-bottom-2 duration-500 delay-150 fill-mode-both">
          <SectionTitle>Uso del producto</SectionTitle>
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
            <KpiCard
              label="Rutas consultadas"
              value={usoQ.data?.totalConsultasRutas ?? 0}
              icon={RouteIcon}
              loading={usoQ.isLoading}
              unavailable={usoQ.isError}
              hint={
                usoQ.data
                  ? `${usoQ.data.consultasRutasUltimos30Dias} en 30 días`
                  : undefined
              }
            />
            <KpiCard
              label="Estimaciones de tarifa"
              value={usoQ.data?.totalEstimaciones ?? 0}
              icon={Tag}
              loading={usoQ.isLoading}
              unavailable={usoQ.isError}
              hint={
                usoQ.data
                  ? `${usoQ.data.estimacionesUltimos30Dias} en 30 días`
                  : undefined
              }
            />
            <KpiCard
              label="Cruces registrados"
              value={historialQ.data?.totalCruces ?? 0}
              icon={Activity}
              loading={historialQ.isLoading}
              unavailable={historialQ.isError}
              hint={
                historialQ.data
                  ? `${historialQ.data.usuariosConCruces} usuarios con cruces`
                  : undefined
              }
            />
            <KpiCard
              label="Vehículos registrados"
              value={kpisUsuarios.totalVehiculos}
              icon={Car}
              loading={usuariosQ.isLoading}
              unavailable={usuariosQ.isError}
              hint={`${kpisUsuarios.conVehiculo} usuarios con vehículo`}
            />
          </div>
        </section>

        {/* Gráfico + actividad reciente */}
        <div className="grid lg:grid-cols-3 gap-4 animate-in fade-in slide-in-from-bottom-2 duration-500 delay-200 fill-mode-both">
          <Card className="lg:col-span-2">
            <CardHeader>
              <CardTitle className="text-base">Uso del producto por mes</CardTitle>
              <CardDescription>
                Rutas consultadas y estimaciones de tarifa.
              </CardDescription>
            </CardHeader>
            <CardContent>
              {usoQ.isLoading ? (
                <Skeleton className="h-64 w-full" />
              ) : usoQ.isError ? (
                <EmptyChart>Estadísticas de uso no disponibles</EmptyChart>
              ) : usoMensual.length === 0 ? (
                <EmptyChart>Aún no hay actividad registrada</EmptyChart>
              ) : (
                <UsoChart data={usoMensual} />
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <div>
                <CardTitle className="text-base">Actividad reciente</CardTitle>
                <CardDescription>Últimos cambios administrativos.</CardDescription>
              </div>
              <Button asChild variant="ghost" size="sm">
                <Link to="/auditoria">
                  Ver todo
                  <ArrowRight className="h-4 w-4" />
                </Link>
              </Button>
            </CardHeader>
            <CardContent>
              {auditoriaQ.isLoading ? (
                <div className="space-y-3">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <div key={i} className="flex items-center gap-3">
                      <Skeleton className="h-8 w-8 rounded-full" />
                      <div className="flex-1 space-y-1.5">
                        <Skeleton className="h-3 w-40" />
                        <Skeleton className="h-2 w-24" />
                      </div>
                    </div>
                  ))}
                </div>
              ) : auditoriaQ.isError ? (
                <EmptyChart>Bitácora no disponible</EmptyChart>
              ) : actividadReciente.length === 0 ? (
                <EmptyChart>Sin actividad reciente</EmptyChart>
              ) : (
                <ul className="space-y-3">
                  {actividadReciente.map((r, i) => (
                    <li
                      key={r.id}
                      className="flex items-start gap-3 animate-in fade-in slide-in-from-bottom-1 duration-500 fill-mode-both"
                      style={{ animationDelay: `${i * 60}ms` }}
                    >
                      <div className="h-8 w-8 rounded-full bg-brand-soft flex items-center justify-center shrink-0 text-[11px] font-semibold text-brand">
                        {iniciales(r.usuarioEmail)}
                      </div>
                      <div className="min-w-0 flex-1">
                        <p className="text-sm leading-snug">
                          <span className="font-medium">{nombreActor(r.usuarioEmail)}</span>{" "}
                          <span className="text-muted-foreground">
                            {(ACCION_LABEL[r.accion] ?? r.accion).toLowerCase()}
                          </span>{" "}
                          <span className="text-muted-foreground">
                            {r.entidad?.toLowerCase()}
                          </span>
                        </p>
                        {r.descripcion && (
                          <p className="text-xs text-muted-foreground truncate">
                            {r.descripcion}
                          </p>
                        )}
                      </div>
                      <span className="text-xs text-muted-foreground whitespace-nowrap shrink-0">
                        {tiempoRelativo(r.fecha)}
                      </span>
                    </li>
                  ))}
                </ul>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Accesos rápidos */}
        <section className="space-y-3 animate-in fade-in slide-in-from-bottom-2 duration-500 delay-300 fill-mode-both">
          <SectionTitle>Accesos rápidos</SectionTitle>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
            <QuickAction to="/porticos" icon={Plus} label="Gestionar pórticos" />
            <QuickAction to="/tarifas" icon={Tag} label="Configurar tarifas" />
            <QuickAction to="/reportes" icon={Activity} label="Ver reportes" />
            <QuickAction to="/files" icon={Upload} label="Importar JSONs" />
          </div>
        </section>
      </div>
    </div>
  );
}

interface Saludo {
  texto: string;
  icon: React.ComponentType<{ className?: string }>;
  color: string;
}

function obtenerSaludo(): Saludo {
  const h = new Date().getHours();
  if (h < 12) {
    return { texto: "Buenos días", icon: Sunrise, color: "text-amber-500" };
  }
  if (h < 20) {
    return { texto: "Buenas tardes", icon: Sun, color: "text-amber-500" };
  }
  return { texto: "Buenas noches", icon: Moon, color: "text-indigo-400" };
}

/** Nombre legible a partir del email del actor de un cambio. */
function nombreActor(email: string | null | undefined): string {
  if (!email) return "Sistema";
  const local = email.split("@")[0] ?? "";
  if (!local) return "Sistema";
  return local
    .split(/[.\-_]/)
    .filter(Boolean)
    .map((p) => p.charAt(0).toUpperCase() + p.slice(1))
    .join(" ");
}

const ACCION_LABEL: Record<TipoAccion, string> = {
  CREAR: "Creó",
  ACTUALIZAR: "Actualizó",
  ACTIVAR: "Activó",
  DESACTIVAR: "Desactivó",
  ELIMINAR: "Eliminó",
  CONFIGURAR_TARIFA: "Configuró tarifa de",
  CARGA_MASIVA: "Realizó carga masiva de",
};

function SectionTitle({ children }: { children: React.ReactNode }) {
  return (
    <h2 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
      {children}
    </h2>
  );
}

interface KpiCardProps {
  label: string;
  value?: number;
  valueText?: string;
  icon?: React.ComponentType<{ className?: string }>;
  accent?: string;
  hint?: string;
  to?: string;
  loading?: boolean;
  unavailable?: boolean;
}

function KpiCard({
  label,
  value,
  valueText,
  icon: Icon,
  accent,
  hint,
  to,
  loading,
  unavailable,
}: KpiCardProps) {
  const display = unavailable
    ? "—"
    : valueText ?? (value ?? 0).toLocaleString("es-CL");

  const inner = (
    <Card
      className={`py-4 h-full transition-all duration-200 ${to ? "hover:border-brand/40 hover:bg-muted/40 hover:-translate-y-0.5 hover:shadow-sm" : ""}`}
    >
      <CardContent className="px-4 flex items-start justify-between gap-2">
        <div className="min-w-0">
          <p className="text-xs text-muted-foreground uppercase tracking-wider truncate">
            {label}
          </p>
          {loading ? (
            <Skeleton className="h-8 w-16 mt-1" />
          ) : (
            <p className={`text-2xl font-semibold mt-1 ${accent ?? ""}`}>
              {display}
            </p>
          )}
          {hint && !loading && !unavailable && (
            <p className="text-xs text-muted-foreground mt-0.5 truncate">{hint}</p>
          )}
          {unavailable && !loading && (
            <p className="text-xs text-muted-foreground/70 mt-0.5">No disponible</p>
          )}
        </div>
        {Icon && (
          <div className="h-9 w-9 rounded-md bg-muted flex items-center justify-center shrink-0">
            <Icon className="h-4 w-4 text-muted-foreground" />
          </div>
        )}
      </CardContent>
    </Card>
  );

  if (to) {
    return (
      <Link to={to} className="block">
        {inner}
      </Link>
    );
  }
  return inner;
}

function QuickAction({
  to,
  icon: Icon,
  label,
}: {
  to: string;
  icon: React.ComponentType<{ className?: string }>;
  label: string;
}) {
  return (
    <Button
      asChild
      variant="outline"
      className="group h-auto justify-start gap-3 py-3 px-4 transition-all duration-200 hover:border-brand/40 hover:-translate-y-0.5 hover:shadow-sm"
    >
      <Link to={to}>
        <span className="h-8 w-8 rounded-md bg-brand-soft text-brand flex items-center justify-center shrink-0">
          <Icon className="h-4 w-4" />
        </span>
        <span className="text-sm font-medium">{label}</span>
        <ArrowRight className="h-4 w-4 ml-auto text-muted-foreground transition-transform duration-200 group-hover:translate-x-0.5" />
      </Link>
    </Button>
  );
}

const usoConfig = {
  consultasRutas: { label: "Rutas", color: "var(--brand)" },
  estimaciones: { label: "Estimaciones", color: "oklch(0.72 0.14 263)" },
} satisfies ChartConfig;

function UsoChart({
  data,
}: {
  data: { mes: string; consultasRutas: number; estimaciones: number }[];
}) {
  return (
    <ChartContainer config={usoConfig} className="h-64 w-full">
      <BarChart data={data} margin={{ left: 0, right: 8, top: 8 }}>
        <CartesianGrid vertical={false} strokeDasharray="3 3" />
        <XAxis dataKey="mes" tickLine={false} axisLine={false} tickMargin={8} fontSize={11} />
        <YAxis tickLine={false} axisLine={false} tickMargin={8} fontSize={11} width={32} />
        <ChartTooltip content={<ChartTooltipContent />} />
        <Bar dataKey="consultasRutas" fill="var(--color-consultasRutas)" radius={[4, 4, 0, 0]} />
        <Bar dataKey="estimaciones" fill="var(--color-estimaciones)" radius={[4, 4, 0, 0]} />
      </BarChart>
    </ChartContainer>
  );
}

function EmptyChart({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex h-56 items-center justify-center text-sm text-muted-foreground border border-dashed rounded-md">
      {children}
    </div>
  );
}
