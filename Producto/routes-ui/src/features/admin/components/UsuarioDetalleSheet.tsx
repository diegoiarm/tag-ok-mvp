import { useState } from "react";
import { CalendarDays, Clock, Hash, Mail, Phone, ShieldCheck, Star } from "lucide-react";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
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
import type { Usuario, VehiculoUsuario } from "@/hooks/useUsuarios";
import { useUpdateUserStatus } from "@/hooks/useUsuarios";
import {
  categoriaLabel,
  tipoVehiculoIcon,
  tipoVehiculoLabel,
} from "@/features/admin/lib/vehiculo";
import { formatFecha, formatFechaHora, iniciales, tiempoRelativo } from "@/features/admin/lib/format";

interface Props {
  usuario: Usuario | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function UsuarioDetalleSheet({ usuario, open, onOpenChange }: Props) {
  const [confirmOpen, setConfirmOpen] = useState(false);
  const updateStatus = useUpdateUserStatus();

  if (!usuario) return null;

  const toggleActivo = () => {
    if (usuario.activo) {
      setConfirmOpen(true);
    } else {
      updateStatus.mutate({ userId: usuario.id, activo: true });
    }
  };

  const confirmDesactivar = () => {
    updateStatus.mutate({ userId: usuario.id, activo: false });
    setConfirmOpen(false);
  };

  return (
    <>
      <Sheet open={open} onOpenChange={onOpenChange}>
        <SheetContent className="w-full sm:max-w-md overflow-y-auto">
          <SheetHeader className="pb-2">
            <div className="flex items-start gap-3">
              <Avatar className="h-12 w-12 border border-border">
                <AvatarFallback className="bg-muted text-foreground text-sm font-semibold">
                  {iniciales(usuario.email)}
                </AvatarFallback>
              </Avatar>
              <div className="flex-1 min-w-0">
                <SheetTitle className="truncate text-base">
                  {usuario.email ?? "Sin correo"}
                </SheetTitle>
                <SheetDescription className="flex items-center gap-2 mt-1">
                  <Badge variant={usuario.activo ? "secondary" : "destructive"}>
                    {usuario.activo ? "Activo" : "Inactivo"}
                  </Badge>
                  {usuario.app_metadata?.role === "admin" && (
                    <Badge variant="outline" className="gap-1">
                      <ShieldCheck className="h-3 w-3" /> Admin
                    </Badge>
                  )}
                </SheetDescription>
              </div>
            </div>
          </SheetHeader>

          <div className="px-4 pb-6 space-y-6">
            <section className="rounded-lg border border-border bg-card p-4">
              <div className="flex items-center justify-between">
                <div>
                  <Label
                    htmlFor="activo-switch"
                    className="text-sm font-medium"
                  >
                    {usuario.activo ? "Cuenta activa" : "Cuenta desactivada"}
                  </Label>
                  <p className="text-xs text-muted-foreground mt-1">
                    {usuario.activo
                      ? "El usuario puede iniciar sesión."
                      : "El usuario no puede iniciar sesión."}
                  </p>
                </div>
                <Switch
                  id="activo-switch"
                  checked={usuario.activo}
                  disabled={updateStatus.isPending}
                  onCheckedChange={toggleActivo}
                />
              </div>
              {updateStatus.isError && (
                <p className="text-xs text-destructive mt-2">
                  {(updateStatus.error as Error)?.message ?? "Error al actualizar"}
                </p>
              )}
            </section>

            <section className="space-y-3">
              <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                Información
              </h3>
              <InfoRow icon={Mail} label="Correo" value={usuario.email ?? "—"} />
              {usuario.phone && (
                <InfoRow icon={Phone} label="Teléfono" value={usuario.phone} />
              )}
              <InfoRow icon={Hash} label="ID" value={usuario.id} mono />
              <InfoRow
                icon={CalendarDays}
                label="Registrado"
                value={formatFecha(usuario.created_at)}
              />
              <InfoRow
                icon={Clock}
                label="Último acceso"
                value={
                  usuario.last_sign_in_at
                    ? `${formatFechaHora(usuario.last_sign_in_at)} · ${tiempoRelativo(usuario.last_sign_in_at)}`
                    : "Nunca"
                }
              />
            </section>

            <Separator />

            <section className="space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Vehículos asociados
                </h3>
                <Badge variant="outline">{usuario.vehiculos.length}</Badge>
              </div>

              {usuario.vehiculos.length === 0 ? (
                <p className="text-sm text-muted-foreground py-4 text-center border border-dashed border-border rounded-lg">
                  Sin vehículos registrados
                </p>
              ) : (
                <ul className="space-y-2">
                  {usuario.vehiculos.map((v) => (
                    <VehiculoItem key={v.id} vehiculo={v} />
                  ))}
                </ul>
              )}
            </section>
          </div>
        </SheetContent>
      </Sheet>

      <AlertDialog open={confirmOpen} onOpenChange={setConfirmOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>¿Desactivar usuario?</AlertDialogTitle>
            <AlertDialogDescription>
              <strong>{usuario.email}</strong> no podrá iniciar sesión hasta que
              vuelvas a activar la cuenta. Sus vehículos y datos se conservan.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDesactivar}
              className="bg-destructive text-white hover:bg-destructive/90"
            >
              Desactivar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
}

interface InfoRowProps {
  icon: React.ComponentType<{ className?: string }>;
  label: string;
  value: string;
  mono?: boolean;
}

function InfoRow({ icon: Icon, label, value, mono }: InfoRowProps) {
  return (
    <div className="flex items-start gap-3 text-sm">
      <Icon className="h-4 w-4 text-muted-foreground shrink-0 mt-0.5" />
      <div className="flex-1 min-w-0">
        <p className="text-xs text-muted-foreground">{label}</p>
        <p className={`break-all ${mono ? "font-mono text-xs" : ""}`}>
          {value}
        </p>
      </div>
    </div>
  );
}

function VehiculoItem({ vehiculo }: { vehiculo: VehiculoUsuario }) {
  const Icon = tipoVehiculoIcon(vehiculo.tipo_vehiculo);
  return (
    <li className="flex items-center gap-3 rounded-lg border border-border bg-card p-3">
      <div className="flex h-10 w-10 items-center justify-center rounded-md bg-muted shrink-0">
        <Icon className="h-5 w-5 text-foreground" />
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2">
          <span className="font-mono text-sm font-semibold tracking-wider">
            {vehiculo.patente}
          </span>
          {vehiculo.es_principal && (
            <Star className="h-3.5 w-3.5 text-amber-500 fill-amber-500" />
          )}
        </div>
        <p className="text-xs text-muted-foreground truncate">
          {tipoVehiculoLabel(vehiculo.tipo_vehiculo)} ·{" "}
          {categoriaLabel(vehiculo.categoria)}
        </p>
        {vehiculo.alias && (
          <p className="text-xs text-muted-foreground italic truncate">
            {vehiculo.alias}
          </p>
        )}
      </div>
      {vehiculo.numero_tag && (
        <Badge variant="outline" className="font-mono">
          TAG {vehiculo.numero_tag}
        </Badge>
      )}
    </li>
  );
}
