import { useState } from "react";
import { Loader2, Save, TriangleAlert } from "lucide-react";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useCreatePortico, useUpdatePortico } from "@/hooks/usePorticos";
import type {
  AutopistaResumen,
  PorticoAdmin,
  PorticoFormInput,
} from "@/types/types";

interface Props {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  /** Pórtico a editar; null = creación. */
  portico: PorticoAdmin | null;
  autopistas: AutopistaResumen[];
}

export function PorticoFormSheet({
  open,
  onOpenChange,
  portico,
  autopistas,
}: Props) {
  const crear = useCreatePortico();
  const actualizar = useUpdatePortico();
  const guardando = crear.isPending || actualizar.isPending;

  const guardar = async (input: PorticoFormInput) => {
    if (portico) {
      await actualizar.mutateAsync({ id: portico.id, input });
    } else {
      await crear.mutateAsync(input);
    }
    onOpenChange(false);
  };

  return (
    <Sheet
      open={open}
      onOpenChange={(o) => {
        if (guardando) return;
        onOpenChange(o);
      }}
    >
      <SheetContent className="w-full sm:max-w-md flex flex-col gap-0 p-0">
        {open && (
          // El `key` remonta el formulario en cada apertura, inicializando el
          // estado desde el pórtico sin necesidad de un efecto de sincronización.
          <FormularioPortico
            key={portico?.id ?? "nuevo"}
            portico={portico}
            autopistas={autopistas}
            guardando={guardando}
            onGuardar={guardar}
            onCancelar={() => onOpenChange(false)}
          />
        )}
      </SheetContent>
    </Sheet>
  );
}

type FormState = {
  codigo: string;
  nombre: string;
  sentido: string;
  latitud: string;
  longitud: string;
  autopistaId: string;
  activo: boolean;
};

function estadoInicial(portico: PorticoAdmin | null): FormState {
  if (!portico) {
    return {
      codigo: "",
      nombre: "",
      sentido: "",
      latitud: "",
      longitud: "",
      autopistaId: "",
      activo: true,
    };
  }
  return {
    codigo: portico.codigo ?? "",
    nombre: portico.nombre ?? "",
    sentido: portico.sentido ?? "",
    latitud: String(portico.latitud ?? ""),
    longitud: String(portico.longitud ?? ""),
    autopistaId: portico.autopistaId ? String(portico.autopistaId) : "",
    activo: portico.activo,
  };
}

interface FormularioProps {
  portico: PorticoAdmin | null;
  autopistas: AutopistaResumen[];
  guardando: boolean;
  onGuardar: (input: PorticoFormInput) => Promise<void>;
  onCancelar: () => void;
}

function FormularioPortico({
  portico,
  autopistas,
  guardando,
  onGuardar,
  onCancelar,
}: FormularioProps) {
  const esEdicion = !!portico;
  const [form, setForm] = useState<FormState>(() => estadoInicial(portico));
  const [error, setError] = useState<string | null>(null);

  const set = (patch: Partial<FormState>) =>
    setForm((prev) => ({ ...prev, ...patch }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const codigo = form.codigo.trim();
    const nombre = form.nombre.trim();

    if (!codigo || !nombre) {
      setError("El código y el nombre son obligatorios.");
      return;
    }
    if (!form.autopistaId) {
      setError("Debes seleccionar una autopista.");
      return;
    }
    const lat = Number(form.latitud.replace(",", "."));
    const lon = Number(form.longitud.replace(",", "."));
    if (!Number.isFinite(lat) || !Number.isFinite(lon)) {
      setError("Latitud y longitud deben ser números válidos.");
      return;
    }

    setError(null);
    try {
      await onGuardar({
        codigo,
        nombre,
        sentido: form.sentido.trim(),
        latitud: lat,
        longitud: lon,
        autopistaId: Number(form.autopistaId),
        activo: form.activo,
      });
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "No se pudo guardar el pórtico.",
      );
    }
  };

  return (
    <>
      <SheetHeader className="border-b p-4">
        <SheetTitle>{esEdicion ? "Editar pórtico" : "Nuevo pórtico"}</SheetTitle>
        <SheetDescription>
          {esEdicion
            ? "Modifica los atributos y la referencia geográfica del pórtico."
            : "Registra un pórtico y asócialo a una autopista concesionada."}
        </SheetDescription>
      </SheetHeader>

      <form onSubmit={handleSubmit} className="flex flex-1 flex-col overflow-hidden">
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          <div className="space-y-1.5">
            <Label htmlFor="portico-autopista">Autopista</Label>
            <Select
              value={form.autopistaId}
              onValueChange={(v) => set({ autopistaId: v })}
              disabled={guardando}
            >
              <SelectTrigger id="portico-autopista" className="w-full">
                <SelectValue placeholder="Selecciona una autopista" />
              </SelectTrigger>
              <SelectContent>
                {autopistas.map((a) => (
                  <SelectItem key={a.id} value={String(a.id)}>
                    {a.nombre} ({a.codigo})
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="portico-codigo">Código</Label>
              <Input
                id="portico-codigo"
                value={form.codigo}
                onChange={(e) => set({ codigo: e.target.value })}
                placeholder="Ej: P0"
                className="font-mono"
                autoFocus
                disabled={guardando}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="portico-sentido">Sentido</Label>
              <Input
                id="portico-sentido"
                value={form.sentido}
                onChange={(e) => set({ sentido: e.target.value })}
                placeholder="Ej: PO / N-S"
                disabled={guardando}
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="portico-nombre">Nombre</Label>
            <Input
              id="portico-nombre"
              value={form.nombre}
              onChange={(e) => set({ nombre: e.target.value })}
              placeholder="Ej: P. San Francisco"
              disabled={guardando}
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="portico-lat">Latitud</Label>
              <Input
                id="portico-lat"
                value={form.latitud}
                onChange={(e) => set({ latitud: e.target.value })}
                placeholder="-33.371363"
                inputMode="decimal"
                className="font-mono"
                disabled={guardando}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="portico-lon">Longitud</Label>
              <Input
                id="portico-lon"
                value={form.longitud}
                onChange={(e) => set({ longitud: e.target.value })}
                placeholder="-70.523380"
                inputMode="decimal"
                className="font-mono"
                disabled={guardando}
              />
            </div>
          </div>

          {esEdicion && (
            <div className="flex items-center justify-between rounded-md border p-3">
              <div>
                <Label htmlFor="portico-activo">Pórtico vigente</Label>
                <p className="text-xs text-muted-foreground mt-0.5">
                  Desactívalo para mantenerlo como histórico sin eliminarlo.
                </p>
              </div>
              <Switch
                id="portico-activo"
                checked={form.activo}
                onCheckedChange={(v) => set({ activo: v })}
                disabled={guardando}
              />
            </div>
          )}

          {error && (
            <div className="flex items-start gap-2 rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
              <TriangleAlert className="h-4 w-4 mt-0.5 shrink-0" />
              <span>{error}</span>
            </div>
          )}
        </div>

        <SheetFooter className="border-t flex-row justify-end gap-2">
          <Button
            type="button"
            variant="outline"
            onClick={onCancelar}
            disabled={guardando}
          >
            Cancelar
          </Button>
          <Button type="submit" disabled={guardando}>
            {guardando ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" />
                Guardando...
              </>
            ) : (
              <>
                <Save className="h-4 w-4" />
                {esEdicion ? "Guardar cambios" : "Crear pórtico"}
              </>
            )}
          </Button>
        </SheetFooter>
      </form>
    </>
  );
}
