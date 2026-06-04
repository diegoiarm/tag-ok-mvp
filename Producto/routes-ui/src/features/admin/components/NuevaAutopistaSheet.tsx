import { useState } from "react";
import { Loader2, Plus, TriangleAlert } from "lucide-react";
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useCreateAutopista } from "@/hooks/useAutopistas";
import type { AutopistaResumen, TipoCobro } from "@/types/types";

interface Props {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  existentes: AutopistaResumen[];
}

export function NuevaAutopistaSheet({ open, onOpenChange, existentes }: Props) {
  const crear = useCreateAutopista();
  const [nombre, setNombre] = useState("");
  const [codigo, setCodigo] = useState("");
  const [tipoCobro, setTipoCobro] = useState<TipoCobro>("PORTICO");
  const [error, setError] = useState<string | null>(null);

  const cerrar = () => {
    // Limpia el formulario al cerrar; la próxima apertura parte en blanco.
    setNombre("");
    setCodigo("");
    setTipoCobro("PORTICO");
    setError(null);
    onOpenChange(false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const nombreLimpio = nombre.trim();
    const codigoLimpio = codigo.trim();

    if (!nombreLimpio || !codigoLimpio) {
      setError("El nombre y el código son obligatorios.");
      return;
    }
    const dupNombre = existentes.some(
      (a) => (a.nombre ?? "").trim().toLowerCase() === nombreLimpio.toLowerCase(),
    );
    if (dupNombre) {
      setError("Ya existe una concesionaria con ese nombre.");
      return;
    }
    const dupCodigo = existentes.some(
      (a) => (a.codigo ?? "").trim().toLowerCase() === codigoLimpio.toLowerCase(),
    );
    if (dupCodigo) {
      setError("Ya existe una concesionaria con ese código.");
      return;
    }

    setError(null);
    try {
      await crear.mutateAsync({
        nombre: nombreLimpio,
        codigo: codigoLimpio,
        tipoCobro,
      });
      cerrar();
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "No se pudo crear la concesionaria.",
      );
    }
  };

  return (
    <Sheet
      open={open}
      onOpenChange={(o) => {
        if (crear.isPending) return;
        if (!o) cerrar();
        else onOpenChange(true);
      }}
    >
      <SheetContent className="w-full sm:max-w-md flex flex-col gap-0 p-0">
        <SheetHeader className="border-b p-4">
          <SheetTitle>Nueva concesionaria</SheetTitle>
          <SheetDescription>
            Registra la autopista con sus datos básicos. Los pórticos y tramos se
            gestionan por separado.
          </SheetDescription>
        </SheetHeader>

        <form
          onSubmit={handleSubmit}
          className="flex flex-1 flex-col overflow-hidden"
        >
          <div className="flex-1 overflow-y-auto p-4 space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="autopista-nombre">Nombre</Label>
              <Input
                id="autopista-nombre"
                value={nombre}
                onChange={(e) => setNombre(e.target.value)}
                placeholder="Ej: Costanera Norte"
                autoFocus
                disabled={crear.isPending}
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="autopista-codigo">Código</Label>
              <Input
                id="autopista-codigo"
                value={codigo}
                onChange={(e) => setCodigo(e.target.value)}
                placeholder="Ej: ACN"
                className="font-mono"
                disabled={crear.isPending}
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="autopista-tipo">Tipo de cobro</Label>
              <Select
                value={tipoCobro}
                onValueChange={(v) => setTipoCobro(v as TipoCobro)}
                disabled={crear.isPending}
              >
                <SelectTrigger id="autopista-tipo" className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="PORTICO">Por pórtico</SelectItem>
                  <SelectItem value="TRAMO">Por tramo</SelectItem>
                </SelectContent>
              </Select>
              <p className="text-xs text-muted-foreground">
                {tipoCobro === "PORTICO"
                  ? "Se cobra un monto fijo al pasar por cada pórtico."
                  : "Se cobra según el trayecto recorrido entre pórticos."}
              </p>
            </div>

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
              onClick={cerrar}
              disabled={crear.isPending}
            >
              Cancelar
            </Button>
            <Button type="submit" disabled={crear.isPending}>
              {crear.isPending ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  Creando...
                </>
              ) : (
                <>
                  <Plus className="h-4 w-4" />
                  Crear concesionaria
                </>
              )}
            </Button>
          </SheetFooter>
        </form>
      </SheetContent>
    </Sheet>
  );
}
