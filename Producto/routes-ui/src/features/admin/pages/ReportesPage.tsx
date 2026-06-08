import { useMemo, useState } from "react";
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Pie,
  PieChart,
  XAxis,
  YAxis,
} from "recharts";
import {
  Activity,
  Car,
  Download,
  FileSpreadsheet,
  FileText,
  MapPin,
  RefreshCw,
  Route,
  Tag,
  TrendingUp,
  Users,
  UserCheck,
  UserX,
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
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { useUsuarios } from "@/hooks/useUsuarios";
import { usePorticosAdmin } from "@/hooks/usePorticos";
import { useAutopistas } from "@/hooks/useAutopistas";
import {
  useEstadisticasHistorial,
  useEstadisticasUso,
} from "@/hooks/useEstadisticas";
import {
  actividadPorBucket,
  calcularKpis,
  distribucionPorTipo,
  filtrarPorRango,
  kpisOperativos,
  registrosPorMes,
} from "@/features/admin/lib/analytics";
import { exportarUsuariosCsv, exportarVehiculosCsv } from "@/features/admin/lib/csv";
import { formatCLP, tiempoRelativo } from "@/features/admin/lib/format";

type Rango = "7" | "30" | "90" | "all";

const PIE_COLORS = [
  "var(--brand)",
  "oklch(0.62 0.18 263)",
  "oklch(0.72 0.14 263)",
  "oklch(0.82 0.1 263)",
  "oklch(0.55 0.12 220)",
  "oklch(0.65 0.1 200)",
];

export function ReportesPage() {
  const { data: usuarios, isLoading, isError, refetch, isFetching } = useUsuarios();
  const { data: porticos, isLoading: loadingPorticos } = usePorticosAdmin();
  const { data: autopistas } = useAutopistas();
  const { data: uso, isLoading: loadingUso, isError: errorUso } = useEstadisticasUso();
  const {
    data: historial,
    isLoading: loadingHistorial,
    isError: errorHistorial,
  } = useEstadisticasHistorial();
  const [rango, setRango] = useState<Rango>("all");

  const usuariosFiltrados = useMemo(
    () => filtrarPorRango(usuarios, rango === "all" ? "all" : Number(rango)),
    [usuarios, rango],
  );

  const kpis = useMemo(() => calcularKpis(usuariosFiltrados), [usuariosFiltrados]);
  const registros = useMemo(() => registrosPorMes(usuariosFiltrados), [usuariosFiltrados]);
  const distribucion = useMemo(() => distribucionPorTipo(usuariosFiltrados), [usuariosFiltrados]);
  const actividad = useMemo(() => actividadPorBucket(usuariosFiltrados), [usuariosFiltrados]);
  const kpisOp = useMemo(
    () => kpisOperativos(porticos, autopistas),
    [porticos, autopistas],
  );
  const usoMensual = useMemo(
    () =>
      (uso?.porMes ?? []).map((p) => ({
        mes: etiquetaMesIso(p.mes),
        consultasRutas: p.consultasRutas,
        estimaciones: p.estimaciones,
      })),
    [uso],
  );

  async function handleExportExcel() {
    const { exportarReporteExcel } = await import("@/features/admin/lib/excel");
    exportarReporteExcel({
      rango,
      usuarios: usuariosFiltrados,
      kpis,
      registros,
      distribucion,
      kpisOp,
      porticos: porticos ?? [],
      autopistas: autopistas ?? [],
      uso,
      historial,
    });
  }

  return (
    <div>
      <div className="mx-auto max-w-7xl px-6 py-8 space-y-6">
        <header className="flex items-start justify-between gap-4 flex-wrap animate-in fade-in slide-in-from-bottom-2 duration-500">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">
              Reportes y estadísticas
            </h1>
            <p className="text-sm text-muted-foreground mt-1">
              Adopción, uso funcional y estado operativo del sistema.
            </p>
          </div>
          <div className="flex items-center gap-2 flex-wrap">
            <Select value={rango} onValueChange={(v) => setRango(v as Rango)}>
              <SelectTrigger className="w-40">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="7">Últimos 7 días</SelectItem>
                <SelectItem value="30">Últimos 30 días</SelectItem>
                <SelectItem value="90">Últimos 90 días</SelectItem>
                <SelectItem value="all">Todo el período</SelectItem>
              </SelectContent>
            </Select>
            <Button
              variant="outline"
              size="sm"
              onClick={() => refetch()}
              disabled={isFetching}
            >
              <RefreshCw className={`h-4 w-4 ${isFetching ? "animate-spin" : ""}`} />
              Actualizar
            </Button>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button size="sm" disabled={!usuariosFiltrados.length}>
                  <Download className="h-4 w-4" />
                  Exportar
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-64">
                <DropdownMenuLabel>Reporte consolidado</DropdownMenuLabel>
                <DropdownMenuItem onClick={handleExportExcel}>
                  <FileText className="h-4 w-4" />
                  Excel (.xlsx) — todas las hojas
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuLabel>Datos sueltos (CSV)</DropdownMenuLabel>
                <DropdownMenuItem
                  onClick={() => exportarUsuariosCsv(usuariosFiltrados)}
                >
                  <FileSpreadsheet className="h-4 w-4" />
                  Usuarios ({usuariosFiltrados.length})
                </DropdownMenuItem>
                <DropdownMenuItem
                  onClick={() => exportarVehiculosCsv(usuariosFiltrados)}
                >
                  <FileSpreadsheet className="h-4 w-4" />
                  Vehículos ({kpis.totalVehiculos})
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </header>

        {isError ? (
          <ErrorState onRetry={refetch} />
        ) : (
          <>
            <section className="animate-in fade-in slide-in-from-bottom-2 duration-500 delay-75 fill-mode-both">
              <SectionTitle>Adopción</SectionTitle>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                <KpiCard
                  label="Usuarios registrados"
                  value={kpis.total}
                  icon={Users}
                  loading={isLoading}
                />
                <KpiCard
                  label="Activos"
                  value={kpis.activos}
                  icon={UserCheck}
                  accent="text-emerald-600 dark:text-emerald-400"
                  loading={isLoading}
                />
                <KpiCard
                  label="Inactivos"
                  value={kpis.inactivos}
                  icon={UserX}
                  accent="text-rose-600 dark:text-rose-400"
                  loading={isLoading}
                />
                <KpiCard
                  label="Con vehículo"
                  value={kpis.conVehiculo}
                  icon={Car}
                  hint={`${kpis.adopcionPct}% de adopción`}
                  loading={isLoading}
                />
              </div>
            </section>

            <section className="animate-in fade-in slide-in-from-bottom-2 duration-500 delay-150 fill-mode-both">
              <SectionTitle>Uso funcional (producto)</SectionTitle>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                <KpiCard
                  label="Rutas consultadas"
                  value={uso?.totalConsultasRutas ?? 0}
                  icon={Route}
                  hint={
                    uso ? `${uso.consultasRutasUltimos30Dias} en 30 días` : undefined
                  }
                  loading={loadingUso}
                  unavailable={errorUso}
                />
                <KpiCard
                  label="Estimaciones de tarifa"
                  value={uso?.totalEstimaciones ?? 0}
                  icon={Tag}
                  hint={
                    uso ? `${uso.estimacionesUltimos30Dias} en 30 días` : undefined
                  }
                  loading={loadingUso}
                  unavailable={errorUso}
                />
                <KpiCard
                  label="Cruces registrados"
                  value={historial?.totalCruces ?? 0}
                  icon={Activity}
                  hint={
                    historial
                      ? `${historial.usuariosConCruces} usuarios con cruces`
                      : undefined
                  }
                  loading={loadingHistorial}
                  unavailable={errorHistorial}
                />
                <KpiCard
                  label="Gasto en peajes"
                  valueText={formatCLP(historial?.totalGasto)}
                  icon={TrendingUp}
                  accent="text-brand"
                  loading={loadingHistorial}
                  unavailable={errorHistorial}
                />
              </div>
            </section>

            <div className="grid lg:grid-cols-2 gap-4 animate-in fade-in slide-in-from-bottom-2 duration-500 delay-200 fill-mode-both">
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">Registros por mes</CardTitle>
                  <CardDescription>
                    Acumulado y nuevos usuarios por mes.
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {isLoading ? (
                    <Skeleton className="h-64 w-full" />
                  ) : registros.length === 0 ? (
                    <EmptyChart>Sin datos en el rango seleccionado</EmptyChart>
                  ) : (
                    <RegistrosChart data={registros} />
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="text-base">
                    Uso de producto por mes
                  </CardTitle>
                  <CardDescription>
                    Rutas consultadas y estimaciones de tarifa.
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {loadingUso ? (
                    <Skeleton className="h-64 w-full" />
                  ) : errorUso ? (
                    <EmptyChart>Estadísticas de uso no disponibles</EmptyChart>
                  ) : usoMensual.length === 0 ? (
                    <EmptyChart>Aún no hay actividad registrada</EmptyChart>
                  ) : (
                    <UsoChart data={usoMensual} />
                  )}
                </CardContent>
              </Card>
            </div>

            <div className="grid lg:grid-cols-2 gap-4 animate-in fade-in slide-in-from-bottom-2 duration-500 delay-200 fill-mode-both">
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">
                    Distribución por tipo de vehículo
                  </CardTitle>
                  <CardDescription>
                    {kpis.totalVehiculos} vehículos registrados
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {isLoading ? (
                    <Skeleton className="h-64 w-full" />
                  ) : distribucion.length === 0 ? (
                    <EmptyChart>Sin vehículos registrados</EmptyChart>
                  ) : (
                    <DistribucionChart data={distribucion} />
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="text-base">
                    Actividad de inicio de sesión
                  </CardTitle>
                  <CardDescription>
                    Usuarios agrupados por la fecha de su último acceso.
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {isLoading ? (
                    <Skeleton className="h-64 w-full" />
                  ) : (
                    <ActividadChart data={actividad} />
                  )}
                </CardContent>
              </Card>
            </div>

            <section className="animate-in fade-in slide-in-from-bottom-2 duration-500 delay-300 fill-mode-both">
              <SectionTitle>Estado operativo</SectionTitle>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                <KpiCard
                  label="Pórticos activos"
                  value={kpisOp.porticosActivos}
                  icon={MapPin}
                  accent="text-emerald-600 dark:text-emerald-400"
                  hint={`de ${kpisOp.totalPorticos} totales`}
                  loading={loadingPorticos}
                />
                <KpiCard
                  label="Pórticos inactivos"
                  value={kpisOp.porticosInactivos}
                  icon={MapPin}
                  accent="text-rose-600 dark:text-rose-400"
                  loading={loadingPorticos}
                />
                <KpiCard
                  label="Con tarifa configurada"
                  value={kpisOp.porticosConTarifa}
                  icon={Tag}
                  hint={`${kpisOp.porticosSinTarifa} pendientes`}
                  loading={loadingPorticos}
                />
                <KpiCard
                  label="Concesionarias"
                  value={kpisOp.totalConcesionarias}
                  icon={Route}
                  loading={loadingPorticos}
                />
              </div>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mt-3">
                <KpiCard
                  label="Cambios últimos 7 días"
                  value={kpisOp.cambiosUltimos7Dias}
                  icon={Activity}
                  loading={loadingPorticos}
                />
                <KpiCard
                  label="Cambios últimos 30 días"
                  value={kpisOp.cambiosUltimos30Dias}
                  icon={Activity}
                  loading={loadingPorticos}
                />
                <KpiCard
                  label="Última actualización"
                  valueText={
                    kpisOp.ultimaActualizacion
                      ? tiempoRelativo(kpisOp.ultimaActualizacion)
                      : "—"
                  }
                  icon={RefreshCw}
                  loading={loadingPorticos}
                />
              </div>
            </section>
          </>
        )}
      </div>
    </div>
  );
}

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

function SectionTitle({ children }: { children: React.ReactNode }) {
  return (
    <h2 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-3">
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
  loading,
  unavailable,
}: KpiCardProps) {
  const display = unavailable
    ? "—"
    : valueText ?? (value ?? 0).toLocaleString("es-CL");
  return (
    <Card className="py-4 transition-all duration-200 hover:-translate-y-0.5 hover:shadow-sm">
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
            <p className="text-xs text-muted-foreground mt-0.5">{hint}</p>
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
}

const registrosConfig = {
  acumulado: { label: "Acumulado", color: "var(--brand)" },
  nuevos: { label: "Nuevos", color: "oklch(0.72 0.14 263)" },
} satisfies ChartConfig;

function RegistrosChart({
  data,
}: {
  data: { mes: string; nuevos: number; acumulado: number }[];
}) {
  return (
    <ChartContainer config={registrosConfig} className="h-64 w-full">
      <AreaChart data={data} margin={{ left: 0, right: 8, top: 8 }}>
        <defs>
          <linearGradient id="fillAcumulado" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="var(--color-acumulado)" stopOpacity={0.4} />
            <stop offset="100%" stopColor="var(--color-acumulado)" stopOpacity={0.05} />
          </linearGradient>
        </defs>
        <CartesianGrid vertical={false} strokeDasharray="3 3" />
        <XAxis dataKey="mes" tickLine={false} axisLine={false} tickMargin={8} fontSize={11} />
        <YAxis tickLine={false} axisLine={false} tickMargin={8} fontSize={11} width={32} />
        <ChartTooltip content={<ChartTooltipContent />} />
        <Area
          type="monotone"
          dataKey="acumulado"
          stroke="var(--color-acumulado)"
          strokeWidth={2}
          fill="url(#fillAcumulado)"
        />
      </AreaChart>
    </ChartContainer>
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

const distribucionConfig: ChartConfig = {
  count: { label: "Vehículos" },
};

function DistribucionChart({
  data,
}: {
  data: { tipo: string; label: string; count: number; pct: number }[];
}) {
  return (
    <ChartContainer config={distribucionConfig} className="h-64 w-full">
      <PieChart>
        <ChartTooltip
          content={
            <ChartTooltipContent
              nameKey="label"
              formatter={(value, _name, item) => (
                <div className="flex items-center justify-between gap-3 w-full">
                  <span>{item?.payload?.label}</span>
                  <span className="font-mono font-medium">
                    {value} ({item?.payload?.pct}%)
                  </span>
                </div>
              )}
            />
          }
        />
        <Pie
          data={data}
          dataKey="count"
          nameKey="label"
          innerRadius={50}
          outerRadius={88}
          strokeWidth={2}
        >
          {data.map((_, i) => (
            <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />
          ))}
        </Pie>
      </PieChart>
    </ChartContainer>
  );
}

const actividadConfig = {
  count: { label: "Usuarios", color: "var(--brand)" },
} satisfies ChartConfig;

function ActividadChart({ data }: { data: { bucket: string; count: number }[] }) {
  return (
    <ChartContainer config={actividadConfig} className="h-56 w-full">
      <BarChart data={data} margin={{ left: 0, right: 8, top: 8 }}>
        <CartesianGrid vertical={false} strokeDasharray="3 3" />
        <XAxis dataKey="bucket" tickLine={false} axisLine={false} tickMargin={8} fontSize={11} />
        <YAxis tickLine={false} axisLine={false} tickMargin={8} fontSize={11} width={32} />
        <ChartTooltip content={<ChartTooltipContent />} />
        <Bar dataKey="count" fill="var(--color-count)" radius={[6, 6, 0, 0]} />
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

function ErrorState({ onRetry }: { onRetry: () => void }) {
  return (
    <Card>
      <CardContent className="flex flex-col items-center justify-center py-16 text-center">
        <p className="text-sm font-medium text-destructive">
          No se pudieron cargar los datos
        </p>
        <p className="text-xs text-muted-foreground mt-1 mb-4">
          Verifica tu conexión o permisos de administrador.
        </p>
        <Button variant="outline" size="sm" onClick={onRetry}>
          <RefreshCw className="h-4 w-4" />
          Reintentar
        </Button>
      </CardContent>
    </Card>
  );
}
