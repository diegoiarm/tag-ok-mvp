import { useMemo, useState } from "react";
import { Car, RefreshCw, Search, Users } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
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
import { useUsuarios, type Usuario } from "@/hooks/useUsuarios";
import { iniciales, tiempoRelativo } from "@/features/admin/lib/format";
import { UsuarioDetalleSheet } from "@/features/admin/components/UsuarioDetalleSheet";

type EstadoFiltro = "todos" | "activos" | "inactivos";
type VehiculosFiltro = "todos" | "con" | "sin";

export function UsuariosPage() {
  const { data: usuarios, isLoading, isError, refetch, isFetching } = useUsuarios();
  const [busqueda, setBusqueda] = useState("");
  const [estado, setEstado] = useState<EstadoFiltro>("todos");
  const [vehiculos, setVehiculos] = useState<VehiculosFiltro>("todos");
  const [seleccionado, setSeleccionado] = useState<Usuario | null>(null);

  const filtrados = useMemo(() => {
    if (!usuarios) return [];
    const q = busqueda.trim().toLowerCase();
    return usuarios.filter((u) => {
      if (estado === "activos" && !u.activo) return false;
      if (estado === "inactivos" && u.activo) return false;
      if (vehiculos === "con" && u.vehiculos.length === 0) return false;
      if (vehiculos === "sin" && u.vehiculos.length > 0) return false;
      if (q) {
        const inEmail = u.email?.toLowerCase().includes(q);
        const inPatente = u.vehiculos.some((v) =>
          v.patente.toLowerCase().includes(q),
        );
        if (!inEmail && !inPatente) return false;
      }
      return true;
    });
  }, [usuarios, busqueda, estado, vehiculos]);

  const stats = useMemo(() => {
    if (!usuarios) return { total: 0, activos: 0, inactivos: 0, conVehiculo: 0 };
    return {
      total: usuarios.length,
      activos: usuarios.filter((u) => u.activo).length,
      inactivos: usuarios.filter((u) => !u.activo).length,
      conVehiculo: usuarios.filter((u) => u.vehiculos.length > 0).length,
    };
  }, [usuarios]);

  return (
    <div className="min-h-screen bg-background text-foreground">
      <div className="mx-auto max-w-7xl px-6 py-8 space-y-6">
        <header className="flex items-start justify-between gap-4">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">Usuarios</h1>
            <p className="text-sm text-muted-foreground mt-1">
              Administra los usuarios registrados en la plataforma TAG OK.
            </p>
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
        </header>

        <section className="grid grid-cols-2 md:grid-cols-4 gap-3">
          <StatCard label="Total" value={stats.total} icon={Users} />
          <StatCard label="Activos" value={stats.activos} accent="text-emerald-600 dark:text-emerald-400" />
          <StatCard label="Inactivos" value={stats.inactivos} accent="text-rose-600 dark:text-rose-400" />
          <StatCard label="Con vehículo" value={stats.conVehiculo} icon={Car} />
        </section>

        <Card>
          <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <CardTitle className="text-base">Listado</CardTitle>
              <CardDescription>
                {filtrados.length} de {usuarios?.length ?? 0} usuarios
              </CardDescription>
            </div>
            <div className="flex flex-col sm:flex-row gap-2 sm:items-center">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Buscar email o patente..."
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                  className="pl-9 sm:w-64"
                />
              </div>
              <Select value={estado} onValueChange={(v) => setEstado(v as EstadoFiltro)}>
                <SelectTrigger className="sm:w-36">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="todos">Todos</SelectItem>
                  <SelectItem value="activos">Activos</SelectItem>
                  <SelectItem value="inactivos">Inactivos</SelectItem>
                </SelectContent>
              </Select>
              <Select
                value={vehiculos}
                onValueChange={(v) => setVehiculos(v as VehiculosFiltro)}
              >
                <SelectTrigger className="sm:w-40">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="todos">Todos los vehículos</SelectItem>
                  <SelectItem value="con">Con vehículo</SelectItem>
                  <SelectItem value="sin">Sin vehículo</SelectItem>
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
              <EmptyState hayUsuarios={!!usuarios && usuarios.length > 0} />
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Usuario</TableHead>
                    <TableHead className="hidden md:table-cell">Estado</TableHead>
                    <TableHead className="hidden md:table-cell">Vehículos</TableHead>
                    <TableHead className="hidden lg:table-cell">Registrado</TableHead>
                    <TableHead>Último acceso</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filtrados.map((u) => (
                    <UsuarioRow
                      key={u.id}
                      usuario={u}
                      onClick={() => setSeleccionado(u)}
                    />
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>

      <UsuarioDetalleSheet
        usuario={seleccionado}
        open={!!seleccionado}
        onOpenChange={(open) => !open && setSeleccionado(null)}
      />
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

function UsuarioRow({
  usuario,
  onClick,
}: {
  usuario: Usuario;
  onClick: () => void;
}) {
  return (
    <TableRow className="cursor-pointer hover:bg-muted/50" onClick={onClick}>
      <TableCell>
        <div className="flex items-center gap-3 min-w-0">
          <Avatar className="h-8 w-8 border border-border">
            <AvatarFallback className="bg-muted text-foreground text-xs font-semibold">
              {iniciales(usuario.email)}
            </AvatarFallback>
          </Avatar>
          <div className="min-w-0">
            <p className="text-sm font-medium truncate">
              {usuario.email ?? "Sin correo"}
            </p>
            <p className="text-xs text-muted-foreground md:hidden">
              {usuario.activo ? "Activo" : "Inactivo"} ·{" "}
              {usuario.vehiculos.length} veh.
            </p>
          </div>
        </div>
      </TableCell>
      <TableCell className="hidden md:table-cell">
        <Badge variant={usuario.activo ? "secondary" : "destructive"}>
          {usuario.activo ? "Activo" : "Inactivo"}
        </Badge>
      </TableCell>
      <TableCell className="hidden md:table-cell">
        {usuario.vehiculos.length === 0 ? (
          <span className="text-sm text-muted-foreground">—</span>
        ) : (
          <div className="flex items-center gap-2">
            <Badge variant="outline" className="gap-1">
              <Car className="h-3 w-3" />
              {usuario.vehiculos.length}
            </Badge>
            <span className="text-xs text-muted-foreground font-mono truncate max-w-32">
              {usuario.vehiculos
                .slice(0, 2)
                .map((v) => v.patente)
                .join(", ")}
              {usuario.vehiculos.length > 2 && "…"}
            </span>
          </div>
        )}
      </TableCell>
      <TableCell className="hidden lg:table-cell text-sm text-muted-foreground">
        {tiempoRelativo(usuario.created_at)}
      </TableCell>
      <TableCell className="text-sm">
        {usuario.last_sign_in_at ? (
          <span className="text-foreground">
            {tiempoRelativo(usuario.last_sign_in_at)}
          </span>
        ) : (
          <span className="text-muted-foreground italic">Nunca</span>
        )}
      </TableCell>
    </TableRow>
  );
}

function LoadingTable() {
  return (
    <div className="p-4 space-y-2">
      {Array.from({ length: 5 }).map((_, i) => (
        <div key={i} className="flex items-center gap-3 p-2">
          <Skeleton className="h-8 w-8 rounded-full" />
          <div className="flex-1 space-y-1.5">
            <Skeleton className="h-3 w-48" />
            <Skeleton className="h-2 w-32" />
          </div>
          <Skeleton className="h-5 w-16" />
        </div>
      ))}
    </div>
  );
}

function EmptyState({ hayUsuarios }: { hayUsuarios: boolean }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <div className="h-12 w-12 rounded-full bg-muted flex items-center justify-center mb-3">
        <Users className="h-5 w-5 text-muted-foreground" />
      </div>
      <p className="text-sm font-medium">
        {hayUsuarios
          ? "Sin resultados con los filtros actuales"
          : "Aún no hay usuarios registrados"}
      </p>
      <p className="text-xs text-muted-foreground mt-1">
        {hayUsuarios
          ? "Ajusta la búsqueda o limpia los filtros."
          : "Cuando se registren usuarios aparecerán aquí."}
      </p>
    </div>
  );
}

function ErrorState({ onRetry }: { onRetry: () => void }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <p className="text-sm font-medium text-destructive">
        No se pudieron cargar los usuarios
      </p>
      <p className="text-xs text-muted-foreground mt-1 mb-4">
        Verifica tu conexión o permisos de administrador.
      </p>
      <Button variant="outline" size="sm" onClick={onRetry}>
        <RefreshCw className="h-4 w-4" />
        Reintentar
      </Button>
    </div>
  );
}
