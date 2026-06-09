import { useRef, useState, type ChangeEvent } from "react";
import {
  CheckCircle2,
  CloudUpload,
  Download,
  FileJson,
  FileSpreadsheet,
  Loader2,
  TriangleAlert,
  XCircle,
} from "lucide-react";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useCrearPorticosMasivo } from "@/hooks/usePorticos";
import {
  descargarPlantillaCsv,
  descargarPlantillaJson,
  parsearArchivoPorticos,
} from "@/features/admin/lib/porticosBulk";
import type { AutopistaResumen, BulkResult, PorticoBulkItem } from "@/types/types";

interface Props {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  autopistas: AutopistaResumen[];
}

type FilaValidada = PorticoBulkItem & { error: string | null };

export function CargaMasivaPorticosSheet({
  open,
  onOpenChange,
  autopistas,
}: Props) {
  const subir = useCrearPorticosMasivo();
  const inputRef = useRef<HTMLInputElement>(null);

  const [nombreArchivo, setNombreArchivo] = useState<string | null>(null);
  const [filas, setFilas] = useState<FilaValidada[]>([]);
  const [errorParseo, setErrorParseo] = useState<string | null>(null);
  const [resultado, setResultado] = useState<BulkResult | null>(null);

  const codigosAutopista = new Set(
    autopistas.map((a) => (a.codigo ?? "").toLowerCase()),
  );

  const validar = (item: PorticoBulkItem): string | null => {
    if (!item.codigo || !item.nombre) return "Falta código o nombre.";
    if (!item.autopistaCodigo) return "Falta código de autopista.";
    if (!codigosAutopista.has(item.autopistaCodigo.toLowerCase()))
      return `Autopista "${item.autopistaCodigo}" no existe.`;
    if (item.latitud === null || item.longitud === null)
      return "Latitud/longitud inválidas.";
    return null;
  };

  const reset = () => {
    setNombreArchivo(null);
    setFilas([]);
    setErrorParseo(null);
    setResultado(null);
  };

  const cerrar = () => {
    if (subir.isPending) return;
    reset();
    onOpenChange(false);
  };

  const handleArchivo = async (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    e.target.value = "";
    if (!file) return;
    setErrorParseo(null);
    setResultado(null);
    try {
      const texto = await file.text();
      const items = parsearArchivoPorticos(file.name, texto);
      setNombreArchivo(file.name);
      setFilas(items.map((it) => ({ ...it, error: validar(it) })));
    } catch (err) {
      setNombreArchivo(file.name);
      setFilas([]);
      setErrorParseo(
        err instanceof Error ? err.message : "No se pudo leer el archivo.",
      );
    }
  };

  const validas = filas.filter((f) => f.error === null);
  const invalidas = filas.length - validas.length;

  const handleSubir = async () => {
    if (validas.length === 0) return;
    try {
      const res = await subir.mutateAsync(
        validas.map((f) => ({
          autopistaCodigo: f.autopistaCodigo,
          codigo: f.codigo,
          nombre: f.nombre,
          sentido: f.sentido,
          latitud: f.latitud,
          longitud: f.longitud,
        })),
      );
      setResultado(res);
    } catch (err) {
      setErrorParseo(
        err instanceof Error ? err.message : "Error al subir los pórticos.",
      );
    }
  };

  return (
    <Sheet open={open} onOpenChange={(o) => (!o ? cerrar() : onOpenChange(true))}>
      <SheetContent className="w-full sm:max-w-lg flex flex-col gap-0 p-0">
        <SheetHeader className="border-b p-4">
          <SheetTitle>Carga masiva de pórticos</SheetTitle>
          <SheetDescription>
            Sube un archivo <code className="font-mono">.json</code> o{" "}
            <code className="font-mono">.csv</code>. Cada pórtico se asocia a una
            autopista por su código.
          </SheetDescription>
        </SheetHeader>

        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => inputRef.current?.click()}
              disabled={subir.isPending}
            >
              <FileSpreadsheet className="h-4 w-4" />
              Seleccionar archivo
            </Button>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="sm" disabled={subir.isPending}>
                  <Download className="h-4 w-4" />
                  Plantilla
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="start">
                <DropdownMenuItem onClick={descargarPlantillaJson}>
                  <FileJson className="h-4 w-4" />
                  JSON
                </DropdownMenuItem>
                <DropdownMenuItem onClick={descargarPlantillaCsv}>
                  <FileSpreadsheet className="h-4 w-4" />
                  CSV
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
            <input
              ref={inputRef}
              type="file"
              accept=".json,.csv,application/json,text/csv"
              onChange={handleArchivo}
              className="hidden"
            />
          </div>

          {nombreArchivo && (
            <p className="text-xs text-muted-foreground">
              Archivo: <span className="font-medium">{nombreArchivo}</span>
            </p>
          )}

          {errorParseo && (
            <div className="flex items-start gap-2 rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
              <TriangleAlert className="h-4 w-4 mt-0.5 shrink-0" />
              <span>{errorParseo}</span>
            </div>
          )}

          {resultado && (
            <div className="rounded-md border p-3 space-y-2">
              <div className="flex items-center gap-2 text-sm font-medium">
                <CheckCircle2 className="h-4 w-4 text-emerald-600" />
                {resultado.creados} pórtico(s) creado(s)
                {resultado.fallidos > 0 && `, ${resultado.fallidos} con error`}
              </div>
              {resultado.errores.length > 0 && (
                <ul className="text-xs text-destructive space-y-0.5 max-h-32 overflow-y-auto">
                  {resultado.errores.map((e, i) => (
                    <li key={i}>· {e}</li>
                  ))}
                </ul>
              )}
            </div>
          )}

          {!resultado && filas.length > 0 && (
            <div className="space-y-2">
              <div className="flex items-center gap-2 text-xs">
                <Badge variant="outline" className="text-emerald-600 border-emerald-200">
                  {validas.length} válidas
                </Badge>
                {invalidas > 0 && (
                  <Badge variant="destructive">{invalidas} con error</Badge>
                )}
              </div>
              <div className="rounded-md border max-h-72 overflow-y-auto">
                <table className="w-full text-xs">
                  <thead className="sticky top-0 bg-muted">
                    <tr className="text-left">
                      <th className="px-2 py-1.5 font-medium">Autopista</th>
                      <th className="px-2 py-1.5 font-medium">Código</th>
                      <th className="px-2 py-1.5 font-medium">Nombre</th>
                      <th className="px-2 py-1.5 font-medium">Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filas.map((f, i) => (
                      <tr key={i} className="border-t">
                        <td className="px-2 py-1.5 font-mono">{f.autopistaCodigo}</td>
                        <td className="px-2 py-1.5 font-mono">{f.codigo}</td>
                        <td className="px-2 py-1.5 truncate max-w-[140px]">{f.nombre}</td>
                        <td className="px-2 py-1.5">
                          {f.error ? (
                            <span className="flex items-center gap-1 text-destructive">
                              <XCircle className="h-3 w-3 shrink-0" />
                              {f.error}
                            </span>
                          ) : (
                            <CheckCircle2 className="h-3 w-3 text-emerald-600" />
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>

        <SheetFooter className="border-t flex-row justify-end gap-2">
          <Button variant="outline" onClick={cerrar} disabled={subir.isPending}>
            {resultado ? "Cerrar" : "Cancelar"}
          </Button>
          {!resultado && (
            <Button
              onClick={handleSubir}
              disabled={subir.isPending || validas.length === 0}
            >
              {subir.isPending ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  Subiendo...
                </>
              ) : (
                <>
                  <CloudUpload className="h-4 w-4" />
                  Crear {validas.length} pórtico(s)
                </>
              )}
            </Button>
          )}
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
