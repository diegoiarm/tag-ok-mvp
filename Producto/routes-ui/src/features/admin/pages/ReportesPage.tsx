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
  AlertTriangle,
  Car,
  Download,
  FileSpreadsheet,
  Hourglass,
  RefreshCw,
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
import {
  actividadPorBucket,
  calcularKpis,
  distribucionPorTipo,
  filtrarPorRango,
  registrosPorMes,
} from "@/features/admin/lib/analytics";
import { exportarUsuariosCsv, exportarVehiculosCsv } from "@/features/admin/lib/csv";

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
  const [rango, setRango] = useState<Rango>("all");

  const usuariosFiltrados = useMemo(
    () => filtrarPorRango(usuarios, rango === "all" ? "all" : Number(rango)),
    [usuarios, rango],
  );

  const kpis = useMemo(() => calcularKpis(usuariosFiltrados), [usuariosFiltrados]);
  const registros = useMemo(() => registrosPorMes(usuariosFiltrados), [usuariosFiltrados]);
  const distribucion = useMemo(() => distribucionPorTipo(usuariosFiltrados), [usuariosFiltrados]);
  const actividad = useMemo(() => actividadPorBucket(usuariosFiltrados), [usuariosFiltrados]);

  return (
    <div>
      <div className="mx-auto max-w-7xl px-6 py-8 space-y-6">
        <header className="flex items-start justify-between gap-4 flex-wrap">
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
              <DropdownMenuContent align="end" className="w-56">
                <DropdownMenuLabel>Exportar a CSV</DropdownMenuLabel>
                <DropdownMenuSeparator />
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
            <section>
              <h2 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-3">
                Adopción
              </h2>
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

            <section>
              <h2 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-3">
                Uso funcional
              </h2>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                <KpiCard
                  label="Vehículos totales"
                  value={kpis.totalVehiculos}
                  icon={Car}
                  loading={isLoading}
                />
                <KpiCard
                  label="Activos última semana"
                  value={kpis.ultimaSemana}
                  icon={TrendingUp}
                  accent="text-brand"
                  loading={isLoading}
                />
                <KpiPlaceholder label="Estimaciones realizadas" />
                <KpiPlaceholder label="Rutas consultadas" />
              </div>
            </section>

            <div className="grid lg:grid-cols-2 gap-4">
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
            </div>

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

            <section>
              <h2 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-3 flex items-center gap-2">
                <Hourglass className="h-3.5 w-3.5" />
                Próximamente
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                <PendienteCard
                  titulo="Estimaciones y rutas"
                  descripcion="Tracking de cálculos de presupuesto y consultas de ruta desde la app móvil."
                />
                <PendienteCard
                  titulo="Uso del historial"
                  descripcion="Cruces de pórticos por usuario, frecuencia y gasto acumulado."
                />
                <PendienteCard
                  titulo="Auditoría de administrador"
                  descripcion="Cambios de configuración, cargas y modificaciones manuales."
                  icon={AlertTriangle}
                />
              </div>
            </section>
          </>
        )}
      </div>
    </div>
  );
}

interface KpiCardProps {
  label: string;
  value: number;
  icon?: React.ComponentType<{ className?: string }>;
  accent?: string;
  hint?: string;
  loading?: boolean;
}

function KpiCard({ label, value, icon: Icon, accent, hint, loading }: KpiCardProps) {
  return (
    <Card className="py-4">
      <CardContent className="px-4 flex items-start justify-between gap-2">
        <div className="min-w-0">
          <p className="text-xs text-muted-foreground uppercase tracking-wider truncate">
            {label}
          </p>
          {loading ? (
            <Skeleton className="h-8 w-16 mt-1" />
          ) : (
            <p className={`text-2xl font-semibold mt-1 ${accent ?? ""}`}>
              {value.toLocaleString("es-CL")}
            </p>
          )}
          {hint && !loading && (
            <p className="text-xs text-muted-foreground mt-0.5">{hint}</p>
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

function KpiPlaceholder({ label }: { label: string }) {
  return (
    <Card className="py-4 border-dashed">
      <CardContent className="px-4 flex items-start justify-between gap-2">
        <div className="min-w-0">
          <p className="text-xs text-muted-foreground uppercase tracking-wider truncate">
            {label}
          </p>
          <p className="text-2xl font-semibold mt-1 text-muted-foreground/50">
            —
          </p>
          <p className="text-xs text-muted-foreground/70 mt-0.5">Próximamente</p>
        </div>
        <div className="h-9 w-9 rounded-md bg-muted/50 flex items-center justify-center shrink-0">
          <Hourglass className="h-4 w-4 text-muted-foreground/50" />
        </div>
      </CardContent>
    </Card>
  );
}

function PendienteCard({
  titulo,
  descripcion,
  icon: Icon = Hourglass,
}: {
  titulo: string;
  descripcion: string;
  icon?: React.ComponentType<{ className?: string }>;
}) {
  return (
    <Card className="border-dashed bg-muted/30">
      <CardContent className="p-4 space-y-2">
        <div className="flex items-center gap-2">
          <Icon className="h-4 w-4 text-muted-foreground" />
          <h3 className="text-sm font-medium">{titulo}</h3>
        </div>
        <p className="text-xs text-muted-foreground">{descripcion}</p>
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
