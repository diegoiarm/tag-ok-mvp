import { useMemo, useState } from "react";
import {
  ArrowRightLeft,
  CheckCircle2,
  CircleDashed,
  MapPin,
  Pencil,
  RefreshCw,
  Receipt,
  Search,
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
import { usePorticosAdmin } from "@/hooks/usePorticos";
import { useAutopistas } from "@/hooks/useAutopistas";
import { useTramos } from "@/hooks/useTarifas";
import {
  TarifaFormSheet,
  type TarifaTarget,
} from "@/features/admin/components/TarifaFormSheet";
import type { PorticoAdmin, TramoAdmin } from "@/types/types";

type Tab = "porticos" | "tramos";

export function TarifasPage() {
  const [tab, setTab] = useState<Tab>("porticos");
  const [target, setTarget] = useState<TarifaTarget | null>(null);
  const [sheetAbierto, setSheetAbierto] = useState(false);

  const abrirEditor = (t: TarifaTarget) => {
    setTarget(t);
    setSheetAbierto(true);
  };

  return (
    <div>
      <div className="mx-auto max-w-7xl px-6 py-8 space-y-6">
        <header className="flex items-start justify-between gap-4 animate-in fade-in slide-in-from-bottom-2 duration-500">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">Tarifas</h1>
            <p className="text-sm text-muted-foreground mt-1">
              Administra los valores de cobro por vehículo y horario de cada
              pórtico y tramo.
            </p>
          </div>
        </header>

        <div className="inline-flex rounded-md border p-1 bg-muted/40 animate-in fade-in slide-in-from-bottom-2 duration-500 delay-75 fill-mode-both">
          <TabButton
            active={tab === "porticos"}
            onClick={() => setTab("porticos")}
            icon={MapPin}
          >
            Pórticos
          </TabButton>
          <TabButton
            active={tab === "tramos"}
            onClick={() => setTab("tramos")}
            icon={ArrowRightLeft}
          >
            Tramos
          </TabButton>
        </div>

        {tab === "porticos" ? (
          <PorticosTab onEditar={abrirEditor} />
        ) : (
          <TramosTab onEditar={abrirEditor} />
        )}
      </div>

      <TarifaFormSheet
        open={sheetAbierto}
        onOpenChange={setSheetAbierto}
        target={target}
      />
    </div>
  );
}

function TabButton({
  active,
  onClick,
  icon: Icon,
  children,
}: {
  active: boolean;
  onClick: () => void;
  icon: React.ComponentType<{ className?: string }>;
  children: React.ReactNode;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`flex items-center gap-2 rounded px-3 py-1.5 text-sm font-medium transition-all active:scale-95 ${
        active
          ? "bg-background shadow-sm text-foreground"
          : "text-muted-foreground hover:text-foreground"
      }`}
    >
      <Icon className="h-4 w-4" />
      {children}
    </button>
  );
}

/* ===================== Pestaña Pórticos ===================== */

function PorticosTab({
  onEditar,
}: {
  onEditar: (t: TarifaTarget) => void;
}) {
  const { data: porticos, isLoading, isError, refetch, isFetching } =
    usePorticosAdmin();
  const { data: autopistas } = useAutopistas();

  const [busqueda, setBusqueda] = useState("");
  const [autopistaId, setAutopistaId] = useState("todas");

  // Pórticos que se cobran individualmente (autopistas tipo PORTICO).
  const tipoCobroPorAutopista = useMemo(() => {
    const map = new Map<number, string>();
    autopistas?.forEach((a) => map.set(a.id, a.tipoCobro));
    return map;
  }, [autopistas]);

  // Un pórtico se cobra directamente salvo que su autopista sea por TRAMO
  // (esos casos se gestionan en la pestaña Tramos). Las autopistas sin
  // `tipoCobro` definido se asumen por pórtico.
  const porticosCobrables = useMemo(() => {
    if (!porticos) return [];
    return porticos.filter(
      (p) =>
        p.autopistaId != null &&
        tipoCobroPorAutopista.get(p.autopistaId) !== "TRAMO",
    );
  }, [porticos, tipoCobroPorAutopista]);

  const filtrados = useMemo(() => {
    const q = busqueda.trim().toLowerCase();
    return porticosCobrables.filter((p) => {
      if (autopistaId !== "todas" && String(p.autopistaId) !== autopistaId)
        return false;
      if (q) {
        const match =
          p.nombre?.toLowerCase().includes(q) ||
          p.codigo?.toLowerCase().includes(q) ||
          p.autopistaNombre?.toLowerCase().includes(q);
        if (!match) return false;
      }
      return true;
    });
  }, [porticosCobrables, busqueda, autopistaId]);

  const autopistasPortico = useMemo(
    () => autopistas?.filter((a) => a.tipoCobro !== "TRAMO") ?? [],
    [autopistas],
  );

  return (
    <Card className="animate-in fade-in slide-in-from-bottom-2 duration-500">
      <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <CardTitle className="text-base">Pórticos con cobro directo</CardTitle>
          <CardDescription>
            {filtrados.length} de {porticosCobrables.length} pórticos
          </CardDescription>
        </div>
        <div className="flex flex-col sm:flex-row gap-2 sm:items-center">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Buscar pórtico..."
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
              {autopistasPortico.map((a) => (
                <SelectItem key={a.id} value={String(a.id)}>
                  {a.nombre}
                </SelectItem>
              ))}
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
        </div>
      </CardHeader>
      <CardContent className="p-0">
        {isError ? (
          <ErrorState onRetry={refetch} />
        ) : isLoading ? (
          <LoadingTable />
        ) : filtrados.length === 0 ? (
          <EmptyState />
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Pórtico</TableHead>
                <TableHead className="hidden md:table-cell">Autopista</TableHead>
                <TableHead>Tarifa</TableHead>
                <TableHead className="w-0" />
              </TableRow>
            </TableHeader>
            <TableBody className="stagger-rows">
              {filtrados.map((p) => (
                <PorticoRow key={p.id} portico={p} onEditar={onEditar} />
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>
    </Card>
  );
}

function PorticoRow({
  portico,
  onEditar,
}: {
  portico: PorticoAdmin;
  onEditar: (t: TarifaTarget) => void;
}) {
  return (
    <TableRow className="transition-colors hover:bg-muted/50">
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
      <TableCell>
        <TarifaBadge configurada={!!portico.tieneTarifa} />
      </TableCell>
      <TableCell className="text-right">
        <Button
          variant="ghost"
          size="sm"
          onClick={() =>
            onEditar({
              tipo: "portico",
              id: portico.id,
              titulo: portico.nombre,
              subtitulo: `${portico.codigo} · ${portico.autopistaNombre ?? ""}`,
            })
          }
        >
          <Pencil className="h-4 w-4" />
          Editar tarifas
        </Button>
      </TableCell>
    </TableRow>
  );
}

/* ===================== Pestaña Tramos ===================== */

function TramosTab({
  onEditar,
}: {
  onEditar: (t: TarifaTarget) => void;
}) {
  const { data: tramos, isLoading, isError, refetch, isFetching } = useTramos();
  const [busqueda, setBusqueda] = useState("");

  const filtrados = useMemo(() => {
    if (!tramos) return [];
    const q = busqueda.trim().toLowerCase();
    if (!q) return tramos;
    return tramos.filter(
      (t) =>
        t.entradaNombre?.toLowerCase().includes(q) ||
        t.salidaNombre?.toLowerCase().includes(q) ||
        t.entradaCodigo?.toLowerCase().includes(q) ||
        t.salidaCodigo?.toLowerCase().includes(q) ||
        t.autopistaNombre?.toLowerCase().includes(q),
    );
  }, [tramos, busqueda]);

  return (
    <Card className="animate-in fade-in slide-in-from-bottom-2 duration-500">
      <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <CardTitle className="text-base">Tramos con cobro por distancia</CardTitle>
          <CardDescription>
            {filtrados.length} de {tramos?.length ?? 0} tramos
          </CardDescription>
        </div>
        <div className="flex flex-col sm:flex-row gap-2 sm:items-center">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Buscar tramo o autopista..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className="pl-9 sm:w-64"
            />
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={() => refetch()}
            disabled={isFetching}
          >
            <RefreshCw className={`h-4 w-4 ${isFetching ? "animate-spin" : ""}`} />
            Actualizar
          </Button>
        </div>
      </CardHeader>
      <CardContent className="p-0">
        {isError ? (
          <ErrorState onRetry={refetch} />
        ) : isLoading ? (
          <LoadingTable />
        ) : filtrados.length === 0 ? (
          <EmptyState />
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Tramo</TableHead>
                <TableHead className="hidden md:table-cell">Autopista</TableHead>
                <TableHead className="hidden lg:table-cell">Distancia</TableHead>
                <TableHead>Tarifa</TableHead>
                <TableHead className="w-0" />
              </TableRow>
            </TableHeader>
            <TableBody className="stagger-rows">
              {filtrados.map((t) => (
                <TramoRow key={t.id} tramo={t} onEditar={onEditar} />
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>
    </Card>
  );
}

function TramoRow({
  tramo,
  onEditar,
}: {
  tramo: TramoAdmin;
  onEditar: (t: TarifaTarget) => void;
}) {
  const titulo = `${tramo.entradaNombre ?? tramo.entradaCodigo ?? "?"} → ${
    tramo.salidaNombre ?? tramo.salidaCodigo ?? "?"
  }`;
  return (
    <TableRow className="transition-colors hover:bg-muted/50">
      <TableCell>
        <div className="flex items-center gap-3 min-w-0">
          <div className="h-8 w-8 rounded-md bg-brand-soft text-brand flex items-center justify-center shrink-0">
            <ArrowRightLeft className="h-4 w-4" />
          </div>
          <div className="min-w-0">
            <p className="text-sm font-medium truncate">{titulo}</p>
            <p className="text-xs text-muted-foreground font-mono">
              {tramo.entradaCodigo} · {tramo.salidaCodigo}
            </p>
          </div>
        </div>
      </TableCell>
      <TableCell className="hidden md:table-cell text-sm">
        {tramo.autopistaNombre ?? "—"}
      </TableCell>
      <TableCell className="hidden lg:table-cell text-sm text-muted-foreground">
        {tramo.distanciaKm ? `${tramo.distanciaKm.toFixed(1)} km` : "—"}
      </TableCell>
      <TableCell>
        <TarifaBadge configurada={tramo.tieneTarifa} />
      </TableCell>
      <TableCell className="text-right">
        <Button
          variant="ghost"
          size="sm"
          onClick={() =>
            onEditar({
              tipo: "tramo",
              id: tramo.id,
              titulo,
              subtitulo: tramo.autopistaNombre ?? undefined,
            })
          }
        >
          <Pencil className="h-4 w-4" />
          Editar tarifas
        </Button>
      </TableCell>
    </TableRow>
  );
}

/* ===================== Auxiliares ===================== */

function TarifaBadge({ configurada }: { configurada: boolean }) {
  return configurada ? (
    <Badge
      variant="outline"
      className="gap-1 text-emerald-600 border-emerald-200 dark:border-emerald-900"
    >
      <CheckCircle2 className="h-3 w-3" />
      Configurada
    </Badge>
  ) : (
    <Badge variant="outline" className="gap-1 text-muted-foreground">
      <CircleDashed className="h-3 w-3" />
      Pendiente
    </Badge>
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
          <Skeleton className="h-5 w-24" />
        </div>
      ))}
    </div>
  );
}

function EmptyState() {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <div className="h-12 w-12 rounded-full bg-muted flex items-center justify-center mb-3">
        <Receipt className="h-5 w-5 text-muted-foreground" />
      </div>
      <p className="text-sm font-medium">Sin resultados</p>
      <p className="text-xs text-muted-foreground mt-1">
        Ajusta la búsqueda o los filtros.
      </p>
    </div>
  );
}

function ErrorState({ onRetry }: { onRetry: () => void }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <p className="text-sm font-medium text-destructive">
        No se pudo cargar la información
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
