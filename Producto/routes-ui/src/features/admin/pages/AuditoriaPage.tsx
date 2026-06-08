import { useMemo, useState } from "react";
import { RefreshCw, ScrollText, ShieldCheck } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
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
import { Skeleton } from "@/components/ui/skeleton";
import { useAuditoria } from "@/hooks/useAuditoria";
import type { TipoAccion } from "@/api/auditoria";
import { formatFechaHora, iniciales } from "@/features/admin/lib/format";

type VariantBadge =
  | "default"
  | "secondary"
  | "destructive"
  | "outline"
  | "ghost";

const ACCION_LABEL: Record<TipoAccion, string> = {
  CREAR: "Creación",
  ACTUALIZAR: "Actualización",
  ACTIVAR: "Activación",
  DESACTIVAR: "Desactivación",
  ELIMINAR: "Eliminación",
  CONFIGURAR_TARIFA: "Config. tarifa",
  CARGA_MASIVA: "Carga masiva",
};

const ACCION_VARIANT: Record<TipoAccion, VariantBadge> = {
  CREAR: "default",
  ACTUALIZAR: "secondary",
  ACTIVAR: "secondary",
  DESACTIVAR: "outline",
  ELIMINAR: "destructive",
  CONFIGURAR_TARIFA: "secondary",
  CARGA_MASIVA: "outline",
};

type AccionFiltro = "todas" | TipoAccion;

export function AuditoriaPage() {
  const { data: registros, isLoading, isError, refetch, isFetching } = useAuditoria();
  const [accion, setAccion] = useState<AccionFiltro>("todas");

  const filtrados = useMemo(() => {
    if (!registros) return [];
    if (accion === "todas") return registros;
    return registros.filter((r) => r.accion === accion);
  }, [registros, accion]);

  return (
    <div>
      <div className="mx-auto max-w-7xl px-6 py-8 space-y-6">
        <header className="flex items-start justify-between gap-4 flex-wrap animate-in fade-in slide-in-from-bottom-2 duration-500">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight flex items-center gap-2">
              <ShieldCheck className="h-6 w-6 text-brand" />
              Auditoría
            </h1>
            <p className="text-sm text-muted-foreground mt-1">
              Bitácora de cambios administrativos: qué se modificó y qué usuario lo hizo.
            </p>
          </div>
          <div className="flex items-center gap-2 flex-wrap">
            <Select value={accion} onValueChange={(v) => setAccion(v as AccionFiltro)}>
              <SelectTrigger className="w-48">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="todas">Todas las acciones</SelectItem>
                {(Object.keys(ACCION_LABEL) as TipoAccion[]).map((a) => (
                  <SelectItem key={a} value={a}>
                    {ACCION_LABEL[a]}
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
        </header>

        <Card className="animate-in fade-in slide-in-from-bottom-2 duration-500 delay-75 fill-mode-both">
          <CardHeader>
            <CardTitle className="text-base">Registros recientes</CardTitle>
            <CardDescription>
              {isLoading
                ? "Cargando…"
                : `${filtrados.length} ${
                    filtrados.length === 1 ? "registro" : "registros"
                  }`}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <Skeleton className="h-72 w-full" />
            ) : isError ? (
              <EstadoVacio
                titulo="No se pudo cargar la auditoría"
                detalle="Verifica tu conexión o permisos de administrador."
                onRetry={refetch}
              />
            ) : filtrados.length === 0 ? (
              <EstadoVacio
                titulo="Sin registros"
                detalle="Aún no hay cambios administrativos registrados para este filtro."
              />
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="w-44">Fecha</TableHead>
                    <TableHead>Usuario</TableHead>
                    <TableHead className="w-40">Acción</TableHead>
                    <TableHead className="w-28">Entidad</TableHead>
                    <TableHead>Detalle</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody className="stagger-rows">
                  {filtrados.map((r) => (
                    <TableRow key={r.id} className="transition-colors hover:bg-muted/50">
                      <TableCell className="text-xs text-muted-foreground tabular-nums">
                        {formatFechaHora(r.fecha)}
                      </TableCell>
                      <TableCell>
                        {r.usuarioEmail ? (
                          <div className="flex items-center gap-2 min-w-0">
                            <span className="h-6 w-6 rounded-md bg-muted text-[10px] font-semibold flex items-center justify-center shrink-0">
                              {iniciales(r.usuarioEmail)}
                            </span>
                            <span className="truncate text-sm">{r.usuarioEmail}</span>
                          </div>
                        ) : (
                          <span className="text-sm text-muted-foreground">Sistema</span>
                        )}
                      </TableCell>
                      <TableCell>
                        <Badge variant={ACCION_VARIANT[r.accion]}>
                          {ACCION_LABEL[r.accion] ?? r.accion}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-sm">{r.entidad}</TableCell>
                      <TableCell className="text-sm text-muted-foreground">
                        {r.descripcion ?? "—"}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

function EstadoVacio({
  titulo,
  detalle,
  onRetry,
}: {
  titulo: string;
  detalle: string;
  onRetry?: () => void;
}) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <ScrollText className="h-8 w-8 text-muted-foreground/50 mb-3" />
      <p className="text-sm font-medium">{titulo}</p>
      <p className="text-xs text-muted-foreground mt-1 mb-4">{detalle}</p>
      {onRetry && (
        <Button variant="outline" size="sm" onClick={onRetry}>
          <RefreshCw className="h-4 w-4" />
          Reintentar
        </Button>
      )}
    </div>
  );
}
