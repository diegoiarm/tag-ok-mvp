import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
  Building2,
  Loader2,
  MapPinned,
  Plus,
  RefreshCw,
  Route as RouteIcon,
  Search,
  Trash2,
  TriangleAlert,
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
import { useAutopistas, useDeleteAutopista } from "@/hooks/useAutopistas";
import { AutopistaDetalleSheet } from "@/features/admin/components/AutopistaDetalleSheet";
import type { AutopistaResumen, TipoCobro } from "@/types/types";

function descargarAutopistaJson(autopista: AutopistaResumen) {
  const contenido = JSON.stringify(autopista.raw, null, 2);
  const blob = new Blob([contenido], { type: "application/json" });
  const url = URL.createObjectURL(blob);
  const fecha = new Date().toISOString().slice(0, 10);
  const a = document.createElement("a");
  a.href = url;
  a.download = `${autopista.codigo || "autopista"}_${fecha}.json`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

type TipoCobroFiltro = "todos" | TipoCobro;

export function AutopistasPage() {
  const { data: autopistas, isLoading, isError, refetch, isFetching } = useAutopistas();
  const deleteMutation = useDeleteAutopista();
  const [busqueda, setBusqueda] = useState("");
  const [tipoCobro, setTipoCobro] = useState<TipoCobroFiltro>("todos");
  const [aEliminar, setAEliminar] = useState<AutopistaResumen | null>(null);
  const [errorEliminar, setErrorEliminar] = useState<string | null>(null);
  const [detalle, setDetalle] = useState<AutopistaResumen | null>(null);

  const filtradas = useMemo(() => {
    if (!autopistas) return [];
    const q = busqueda.trim().toLowerCase();
    return autopistas.filter((a) => {
      if (tipoCobro !== "todos" && a.tipoCobro !== tipoCobro) return false;
      if (q) {
        const inNombre = a.nombre?.toLowerCase().includes(q);
        const inCodigo = a.codigo?.toLowerCase().includes(q);
        if (!inNombre && !inCodigo) return false;
      }
      return true;
    });
  }, [autopistas, busqueda, tipoCobro]);

  const stats = useMemo(() => {
    if (!autopistas) {
      return { total: 0, porPortico: 0, porTramo: 0, porticos: 0 };
    }
    return {
      total: autopistas.length,
      porPortico: autopistas.filter((a) => a.tipoCobro === "PORTICO").length,
      porTramo: autopistas.filter((a) => a.tipoCobro === "TRAMO").length,
      porticos: autopistas.reduce((acc, a) => acc + a.totalPorticos, 0),
    };
  }, [autopistas]);

  const confirmarEliminacion = async () => {
    if (!aEliminar) return;
    setErrorEliminar(null);
    try {
      await deleteMutation.mutateAsync(aEliminar.id);
      setAEliminar(null);
    } catch (err) {
      setErrorEliminar(
        err instanceof Error ? err.message : "No se pudo eliminar la autopista",
      );
    }
  };

  return (
    <div>
      <div className="mx-auto max-w-7xl px-6 py-8 space-y-6">
        <header className="flex items-start justify-between gap-4">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">Concesionarios</h1>
            <p className="text-sm text-muted-foreground mt-1">
              Administra las autopistas concesionadas y sus pórticos asociados.
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
            <Button asChild size="sm">
              <Link to="/files">
                <Plus className="h-4 w-4" />
                Nueva autopista
              </Link>
            </Button>
          </div>
        </header>

        <section className="grid grid-cols-2 md:grid-cols-4 gap-3">
          <StatCard label="Total" value={stats.total} icon={Building2} />
          <StatCard label="Por pórtico" value={stats.porPortico} icon={MapPinned} />
          <StatCard label="Por tramo" value={stats.porTramo} icon={RouteIcon} />
          <StatCard label="Pórticos totales" value={stats.porticos} />
        </section>

        <Card>
          <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <CardTitle className="text-base">Listado</CardTitle>
              <CardDescription>
                {filtradas.length} de {autopistas?.length ?? 0} autopistas
              </CardDescription>
            </div>
            <div className="flex flex-col sm:flex-row gap-2 sm:items-center">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Buscar por nombre o código..."
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                  className="pl-9 sm:w-64"
                />
              </div>
              <Select
                value={tipoCobro}
                onValueChange={(v) => setTipoCobro(v as TipoCobroFiltro)}
              >
                <SelectTrigger className="sm:w-44">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="todos">Todos los cobros</SelectItem>
                  <SelectItem value="PORTICO">Por pórtico</SelectItem>
                  <SelectItem value="TRAMO">Por tramo</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardHeader>
          <CardContent className="p-0">
            {isError ? (
              <ErrorState onRetry={refetch} />
            ) : isLoading ? (
              <LoadingTable />
            ) : filtradas.length === 0 ? (
              <EmptyState hayAutopistas={!!autopistas && autopistas.length > 0} />
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Autopista</TableHead>
                    <TableHead className="hidden md:table-cell">Código</TableHead>
                    <TableHead className="hidden md:table-cell">Tipo de cobro</TableHead>
                    <TableHead className="hidden lg:table-cell">Pórticos</TableHead>
                    <TableHead className="hidden lg:table-cell">Tramos</TableHead>
                    <TableHead className="w-0" />
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filtradas.map((a) => (
                    <AutopistaRow
                      key={a.id}
                      autopista={a}
                      onSelect={() => setDetalle(a)}
                      onDelete={() => {
                        setErrorEliminar(null);
                        setAEliminar(a);
                      }}
                    />
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>

      <AutopistaDetalleSheet
        autopista={detalle}
        open={!!detalle}
        onOpenChange={(open) => !open && setDetalle(null)}
        onExport={descargarAutopistaJson}
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
            <AlertDialogTitle>Eliminar autopista</AlertDialogTitle>
            <AlertDialogDescription>
              Se eliminará <strong>{aEliminar?.nombre}</strong> ({aEliminar?.codigo})
              junto con sus {aEliminar?.totalPorticos ?? 0} pórticos y{" "}
              {aEliminar?.totalTramos ?? 0} tramos. Esta acción no se puede
              deshacer.
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
  accent?: string;
}

function StatCard({ label, value, icon: Icon, accent }: StatCardProps) {
  return (
    <Card className="py-4">
      <CardContent className="px-4 flex items-center justify-between">
        <div>
          <p className="text-xs text-muted-foreground uppercase tracking-wider">
            {label}
          </p>
          <p className={`text-2xl font-semibold mt-1 ${accent ?? ""}`}>
            {value}
          </p>
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

function AutopistaRow({
  autopista,
  onSelect,
  onDelete,
}: {
  autopista: AutopistaResumen;
  onSelect: () => void;
  onDelete: () => void;
}) {
  const esPortico = autopista.tipoCobro === "PORTICO";
  return (
    <TableRow className="cursor-pointer hover:bg-muted/50" onClick={onSelect}>
      <TableCell>
        <div className="flex items-center gap-3 min-w-0">
          <div className="h-8 w-8 rounded-md bg-brand-soft text-brand flex items-center justify-center shrink-0">
            <Building2 className="h-4 w-4" />
          </div>
          <div className="min-w-0">
            <p className="text-sm font-medium truncate">{autopista.nombre}</p>
            <p className="text-xs text-muted-foreground md:hidden font-mono">
              {autopista.codigo}
            </p>
          </div>
        </div>
      </TableCell>
      <TableCell className="hidden md:table-cell">
        <span className="text-sm font-mono">{autopista.codigo}</span>
      </TableCell>
      <TableCell className="hidden md:table-cell">
        <Badge variant="outline" className="gap-1">
          {esPortico ? (
            <MapPinned className="h-3 w-3" />
          ) : (
            <RouteIcon className="h-3 w-3" />
          )}
          {esPortico ? "Por pórtico" : "Por tramo"}
        </Badge>
      </TableCell>
      <TableCell className="hidden lg:table-cell text-sm">
        {autopista.totalPorticos}
      </TableCell>
      <TableCell className="hidden lg:table-cell text-sm">
        {autopista.totalTramos}
      </TableCell>
      <TableCell className="text-right">
        <Button
          variant="ghost"
          size="icon-sm"
          onClick={(e) => {
            e.stopPropagation();
            onDelete();
          }}
          title="Eliminar autopista"
        >
          <Trash2 className="h-4 w-4 text-destructive" />
        </Button>
      </TableCell>
    </TableRow>
  );
}

function LoadingTable() {
  return (
    <div className="p-4 space-y-2">
      {Array.from({ length: 5 }).map((_, i) => (
        <div key={i} className="flex items-center gap-3 p-2">
          <Skeleton className="h-8 w-8 rounded-md" />
          <div className="flex-1 space-y-1.5">
            <Skeleton className="h-3 w-48" />
            <Skeleton className="h-2 w-32" />
          </div>
          <Skeleton className="h-5 w-24" />
        </div>
      ))}
    </div>
  );
}

function EmptyState({ hayAutopistas }: { hayAutopistas: boolean }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <div className="h-12 w-12 rounded-full bg-muted flex items-center justify-center mb-3">
        <Building2 className="h-5 w-5 text-muted-foreground" />
      </div>
      <p className="text-sm font-medium">
        {hayAutopistas
          ? "Sin resultados con los filtros actuales"
          : "Aún no hay autopistas registradas"}
      </p>
      <p className="text-xs text-muted-foreground mt-1 mb-4">
        {hayAutopistas
          ? "Ajusta la búsqueda o limpia los filtros."
          : "Sube un JSON de autopista para empezar."}
      </p>
      {!hayAutopistas && (
        <Button asChild size="sm" variant="outline">
          <Link to="/files">
            <Plus className="h-4 w-4" />
            Subir JSON
          </Link>
        </Button>
      )}
    </div>
  );
}

function ErrorState({ onRetry }: { onRetry: () => void }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <p className="text-sm font-medium text-destructive">
        No se pudieron cargar las autopistas
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
