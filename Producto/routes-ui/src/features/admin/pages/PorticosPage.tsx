import { useMemo, useState } from "react";
import {
  CircleSlash,
  Loader2,
  MapPin,
  Pencil,
  Plus,
  Power,
  RefreshCw,
  Search,
  Trash2,
  TriangleAlert,
  Upload,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import {
  useCambiarEstadoPortico,
  useDeletePortico,
  usePorticosAdmin,
} from "@/hooks/usePorticos";
import { useAutopistas } from "@/hooks/useAutopistas";
import { PorticoFormSheet } from "@/features/admin/components/PorticoFormSheet";
import { CargaMasivaPorticosSheet } from "@/features/admin/components/CargaMasivaPorticosSheet";
import type { PorticoAdmin } from "@/types/types";

type EstadoFiltro = "todos" | "activos" | "inactivos";

export function PorticosPage() {
  const { data: porticos, isLoading, isError, refetch, isFetching } =
    usePorticosAdmin();
  const { data: autopistas } = useAutopistas();
  const cambiarEstado = useCambiarEstadoPortico();
  const deleteMutation = useDeletePortico();

  const [busqueda, setBusqueda] = useState("");
  const [autopistaId, setAutopistaId] = useState<string>("todas");
  const [estado, setEstado] = useState<EstadoFiltro>("todos");

  const [formAbierto, setFormAbierto] = useState(false);
  const [editando, setEditando] = useState<PorticoAdmin | null>(null);
  const [cargaAbierta, setCargaAbierta] = useState(false);
  const [aEliminar, setAEliminar] = useState<PorticoAdmin | null>(null);
  const [errorEliminar, setErrorEliminar] = useState<string | null>(null);

  const filtrados = useMemo(() => {
    if (!porticos) return [];
    const q = busqueda.trim().toLowerCase();
    return porticos.filter((p) => {
      if (estado === "activos" && !p.activo) return false;
      if (estado === "inactivos" && p.activo) return false;
      if (autopistaId !== "todas" && String(p.autopistaId) !== autopistaId)
        return false;
      if (q) {
        const inNombre = p.nombre?.toLowerCase().includes(q);
        const inCodigo = p.codigo?.toLowerCase().includes(q);
        const inAutopista = p.autopistaNombre?.toLowerCase().includes(q);
        if (!inNombre && !inCodigo && !inAutopista) return false;
      }
      return true;
    });
  }, [porticos, busqueda, autopistaId, estado]);

  const stats = useMemo(() => {
    if (!porticos) return { total: 0, activos: 0, inactivos: 0, autopistas: 0 };
    return {
      total: porticos.length,
      activos: porticos.filter((p) => p.activo).length,
      inactivos: porticos.filter((p) => !p.activo).length,
      autopistas: new Set(porticos.map((p) => p.autopistaId)).size,
    };
  }, [porticos]);

  const abrirNuevo = () => {
    setEditando(null);
    setFormAbierto(true);
  };

  const abrirEdicion = (p: PorticoAdmin) => {
    setEditando(p);
    setFormAbierto(true);
  };

  const confirmarEliminacion = async () => {
    if (!aEliminar) return;
    setErrorEliminar(null);
    try {
      await deleteMutation.mutateAsync(aEliminar.id);
      setAEliminar(null);
    } catch (err) {
      setErrorEliminar(
        err instanceof Error ? err.message : "No se pudo eliminar el pórtico",
      );
    }
  };

  const toggleEstado = (p: PorticoAdmin) => {
    cambiarEstado.mutate({ id: p.id, activo: !p.activo });
  };

  return (
    <div>
      <div className="mx-auto max-w-7xl px-6 py-8 space-y-6">
        <header className="flex items-start justify-between gap-4">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">Pórticos</h1>
            <p className="text-sm text-muted-foreground mt-1">
              Registra y mantiene los pórticos de cada autopista concesionada.
            </p>
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => refetch()}
              disabled={isFetching}
            >
              <RefreshCw className={`h-4 w-4 ${isFetching ? "animate-spin" : ""}`} />
              Actualizar
            </Button>
            <Button variant="outline" size="sm" onClick={() => setCargaAbierta(true)}>
              <Upload className="h-4 w-4" />
              Carga masiva
            </Button>
            <Button size="sm" onClick={abrirNuevo}>
              <Plus className="h-4 w-4" />
              Nuevo pórtico
            </Button>
          </div>
        </header>

        <section className="grid grid-cols-2 md:grid-cols-4 gap-3">
          <StatCard label="Total" value={stats.total} icon={MapPin} />
          <StatCard label="Vigentes" value={stats.activos} icon={Power} />
          <StatCard label="Inactivos" value={stats.inactivos} icon={CircleSlash} />
          <StatCard label="Autopistas" value={stats.autopistas} />
        </section>

        <Card>
          <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <CardTitle className="text-base">Listado</CardTitle>
              <CardDescription>
                {filtrados.length} de {porticos?.length ?? 0} pórticos
              </CardDescription>
            </div>
            <div className="flex flex-col sm:flex-row gap-2 sm:items-center">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Buscar por nombre, código o autopista..."
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                  className="pl-9 sm:w-64"
                />
              </div>
              <Select value={autopistaId} onValueChange={setAutopistaId}>
                <SelectTrigger className="sm:w-48">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="todas">Todas las autopistas</SelectItem>
                  {autopistas?.map((a) => (
                    <SelectItem key={a.id} value={String(a.id)}>
                      {a.nombre}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <Select
                value={estado}
                onValueChange={(v) => setEstado(v as EstadoFiltro)}
              >
                <SelectTrigger className="sm:w-36">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="todos">Todos</SelectItem>
                  <SelectItem value="activos">Vigentes</SelectItem>
                  <SelectItem value="inactivos">Inactivos</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardHeader>
          <CardContent className="p-0">
            {isError ? (
              <ErrorState onRetry={refetch} />
            ) : isLoading ? (
              <LoadingTable />
            ) : filtrados.length === 0 ? (
              <EmptyState hayPorticos={!!porticos && porticos.length > 0} onNuevo={abrirNuevo} />
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Pórtico</TableHead>
                    <TableHead className="hidden md:table-cell">Autopista</TableHead>
                    <TableHead className="hidden lg:table-cell">Sentido</TableHead>
                    <TableHead className="hidden lg:table-cell">Coordenadas</TableHead>
                    <TableHead>Estado</TableHead>
                    <TableHead className="w-0" />
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filtrados.map((p) => (
                    <PorticoRow
                      key={p.id}
                      portico={p}
                      onEdit={() => abrirEdicion(p)}
                      onToggle={() => toggleEstado(p)}
                      onDelete={() => {
                        setErrorEliminar(null);
                        setAEliminar(p);
                      }}
                    />
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>

      <PorticoFormSheet
        open={formAbierto}
        onOpenChange={setFormAbierto}
        portico={editando}
        autopistas={autopistas ?? []}
      />

      <CargaMasivaPorticosSheet
        open={cargaAbierta}
        onOpenChange={setCargaAbierta}
        autopistas={autopistas ?? []}
      />

      <AlertDialog
        open={!!aEliminar}
        onOpenChange={(open) => {
          if (!open && !deleteMutation.isPending) {
            setAEliminar(null);
            setErrorEliminar(null);
          }
        }}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Eliminar pórtico</AlertDialogTitle>
            <AlertDialogDescription>
              Se eliminará <strong>{aEliminar?.nombre}</strong> (
              {aEliminar?.codigo}) de forma permanente. Si solo quieres dejar de
              usarlo, considera desactivarlo en su lugar.
            </AlertDialogDescription>
          </AlertDialogHeader>
          {errorEliminar && (
            <div className="flex items-start gap-2 rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
              <TriangleAlert className="h-4 w-4 mt-0.5 shrink-0" />
              <span>{errorEliminar}</span>
            </div>
          )}
          <AlertDialogFooter>
            <AlertDialogCancel disabled={deleteMutation.isPending}>
              Cancelar
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={(e) => {
                e.preventDefault();
                confirmarEliminacion();
              }}
              disabled={deleteMutation.isPending}
              className="bg-destructive text-white hover:bg-destructive/90"
            >
              {deleteMutation.isPending ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  Eliminando...
                </>
              ) : (
                <>
                  <Trash2 className="h-4 w-4" />
                  Eliminar
                </>
              )}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}

interface StatCardProps {
  label: string;
  value: number;
  icon?: React.ComponentType<{ className?: string }>;
}

function StatCard({ label, value, icon: Icon }: StatCardProps) {
  return (
    <Card className="py-4">
      <CardContent className="px-4 flex items-center justify-between">
        <div>
          <p className="text-xs text-muted-foreground uppercase tracking-wider">
            {label}
          </p>
          <p className="text-2xl font-semibold mt-1">{value}</p>
        </div>
        {Icon && (
          <div className="h-9 w-9 rounded-md bg-muted flex items-center justify-center">
            <Icon className="h-4 w-4 text-muted-foreground" />
          </div>
        )}
      </CardContent>
    </Card>
  );
}

function PorticoRow({
  portico,
  onEdit,
  onToggle,
  onDelete,
}: {
  portico: PorticoAdmin;
  onEdit: () => void;
  onToggle: () => void;
  onDelete: () => void;
}) {
  const hayCoords =
    Number.isFinite(portico.latitud) &&
    Number.isFinite(portico.longitud) &&
    (portico.latitud !== 0 || portico.longitud !== 0);

  return (
    <TableRow className="hover:bg-muted/50">
      <TableCell>
        <div className="flex items-center gap-3 min-w-0">
          <div className="h-8 w-8 rounded-md bg-brand-soft text-brand flex items-center justify-center shrink-0">
            <MapPin className="h-4 w-4" />
          </div>
          <div className="min-w-0">
            <p className="text-sm font-medium truncate">{portico.nombre}</p>
            <p className="text-xs text-muted-foreground font-mono">
              {portico.codigo}
            </p>
          </div>
        </div>
      </TableCell>
      <TableCell className="hidden md:table-cell text-sm">
        {portico.autopistaNombre ?? "—"}
      </TableCell>
      <TableCell className="hidden lg:table-cell">
        {portico.sentido ? (
          <Badge variant="secondary" className="text-xs">
            {portico.sentido}
          </Badge>
        ) : (
          <span className="text-xs text-muted-foreground">—</span>
        )}
      </TableCell>
      <TableCell className="hidden lg:table-cell text-xs font-mono text-muted-foreground">
        {hayCoords
          ? `${portico.latitud.toFixed(5)}, ${portico.longitud.toFixed(5)}`
          : "Sin coordenadas"}
      </TableCell>
      <TableCell>
        <Badge
          variant="outline"
          className={
            portico.activo
              ? "gap-1 text-emerald-600 border-emerald-200 dark:border-emerald-900"
              : "gap-1 text-muted-foreground"
          }
        >
          {portico.activo ? "Vigente" : "Inactivo"}
        </Badge>
      </TableCell>
      <TableCell className="text-right">
        <div className="flex items-center justify-end gap-1">
          <Button
            variant="ghost"
            size="icon-sm"
            onClick={onToggle}
            title={portico.activo ? "Desactivar" : "Activar"}
          >
            <Power
              className={`h-4 w-4 ${portico.activo ? "text-emerald-600" : "text-muted-foreground"}`}
            />
          </Button>
          <Button variant="ghost" size="icon-sm" onClick={onEdit} title="Editar">
            <Pencil className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon-sm"
            onClick={onDelete}
            title="Eliminar"
          >
            <Trash2 className="h-4 w-4 text-destructive" />
          </Button>
        </div>
      </TableCell>
    </TableRow>
  );
}

function LoadingTable() {
  return (
    <div className="p-4 space-y-2">
      {Array.from({ length: 6 }).map((_, i) => (
        <div key={i} className="flex items-center gap-3 p-2">
          <Skeleton className="h-8 w-8 rounded-md" />
          <div className="flex-1 space-y-1.5">
            <Skeleton className="h-3 w-48" />
            <Skeleton className="h-2 w-32" />
          </div>
          <Skeleton className="h-5 w-20" />
        </div>
      ))}
    </div>
  );
}

function EmptyState({
  hayPorticos,
  onNuevo,
}: {
  hayPorticos: boolean;
  onNuevo: () => void;
}) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <div className="h-12 w-12 rounded-full bg-muted flex items-center justify-center mb-3">
        <MapPin className="h-5 w-5 text-muted-foreground" />
      </div>
      <p className="text-sm font-medium">
        {hayPorticos
          ? "Sin resultados con los filtros actuales"
          : "Aún no hay pórticos registrados"}
      </p>
      <p className="text-xs text-muted-foreground mt-1 mb-4">
        {hayPorticos
          ? "Ajusta la búsqueda o limpia los filtros."
          : "Crea un pórtico o usa la carga masiva para empezar."}
      </p>
      {!hayPorticos && (
        <Button size="sm" variant="outline" onClick={onNuevo}>
          <Plus className="h-4 w-4" />
          Nuevo pórtico
        </Button>
      )}
    </div>
  );
}

function ErrorState({ onRetry }: { onRetry: () => void }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <p className="text-sm font-medium text-destructive">
        No se pudieron cargar los pórticos
      </p>
      <p className="text-xs text-muted-foreground mt-1 mb-4">
        Revisa que el servicio backend esté disponible.
      </p>
      <Button variant="outline" size="sm" onClick={onRetry}>
        <RefreshCw className="h-4 w-4" />
        Reintentar
      </Button>
    </div>
  );
}
