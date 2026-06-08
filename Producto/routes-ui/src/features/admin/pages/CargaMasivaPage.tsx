import { useState } from "react";
import { Building2, Download, MapPin, Upload } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useAutopistas } from "@/hooks/useAutopistas";
import { CargaConcesionariasSheet } from "@/features/admin/components/CargaConcesionariasSheet";
import { CargaMasivaPorticosSheet } from "@/features/admin/components/CargaMasivaPorticosSheet";
import { descargarPlantillaAutopista } from "@/features/admin/lib/autopistaTemplate";
import {
  descargarPlantillaCsv,
  descargarPlantillaJson,
} from "@/features/admin/lib/porticosBulk";

type Flujo = "concesionarias" | "porticos" | null;

export function CargaMasivaPage() {
  const { data: autopistas } = useAutopistas();
  const [flujo, setFlujo] = useState<Flujo>(null);

  return (
    <div>
      <div className="mx-auto max-w-5xl px-6 py-8 space-y-6">
        <header className="animate-in fade-in slide-in-from-bottom-2 duration-500">
          <h1 className="text-2xl font-semibold tracking-tight">
            Carga masiva de datos
          </h1>
          <p className="text-sm text-muted-foreground mt-1">
            Importa datos al sistema desde archivos. Elige qué tipo de información
            quieres cargar.
          </p>
        </header>

        <div className="grid sm:grid-cols-2 gap-4 animate-in fade-in slide-in-from-bottom-2 duration-500 delay-75 fill-mode-both">
          <TipoCargaCard
            icon={Building2}
            titulo="Concesionarias"
            descripcion="Sube el JSON completo de una autopista: metadatos, pórticos o tramos y sus tarifas."
            formatos="JSON"
            onCargar={() => setFlujo("concesionarias")}
            plantilla={
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="outline" size="sm">
                    <Download className="h-4 w-4" />
                    Plantilla
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem
                    onClick={() => descargarPlantillaAutopista("PORTICO")}
                  >
                    Cobro por pórtico
                  </DropdownMenuItem>
                  <DropdownMenuItem
                    onClick={() => descargarPlantillaAutopista("TRAMO")}
                  >
                    Cobro por tramo
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            }
          />

          <TipoCargaCard
            icon={MapPin}
            titulo="Pórticos"
            descripcion="Añade pórticos a autopistas existentes a partir de un archivo. Cada fila se asocia por el código de autopista."
            formatos="JSON · CSV"
            onCargar={() => setFlujo("porticos")}
            plantilla={
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="outline" size="sm">
                    <Download className="h-4 w-4" />
                    Plantilla
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem onClick={descargarPlantillaJson}>
                    JSON
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={descargarPlantillaCsv}>
                    CSV
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            }
          />
        </div>
      </div>

      <CargaConcesionariasSheet
        open={flujo === "concesionarias"}
        onOpenChange={(o) => !o && setFlujo(null)}
      />
      <CargaMasivaPorticosSheet
        open={flujo === "porticos"}
        onOpenChange={(o) => !o && setFlujo(null)}
        autopistas={autopistas ?? []}
      />
    </div>
  );
}

interface TipoCargaCardProps {
  icon: React.ComponentType<{ className?: string }>;
  titulo: string;
  descripcion: string;
  formatos: string;
  onCargar: () => void;
  plantilla: React.ReactNode;
}

function TipoCargaCard({
  icon: Icon,
  titulo,
  descripcion,
  formatos,
  onCargar,
  plantilla,
}: TipoCargaCardProps) {
  return (
    <Card className="flex flex-col transition-all duration-200 hover:border-brand/40 hover:shadow-sm">
      <CardHeader>
        <div className="flex items-center gap-3">
          <div className="h-10 w-10 rounded-md bg-brand-soft text-brand flex items-center justify-center shrink-0">
            <Icon className="h-5 w-5" />
          </div>
          <div>
            <CardTitle className="text-base">{titulo}</CardTitle>
            <CardDescription className="text-xs font-mono mt-0.5">
              {formatos}
            </CardDescription>
          </div>
        </div>
      </CardHeader>
      <CardContent className="flex flex-1 flex-col justify-between gap-4">
        <p className="text-sm text-muted-foreground">{descripcion}</p>
        <div className="flex items-center gap-2">
          <Button onClick={onCargar} size="sm">
            <Upload className="h-4 w-4" />
            Cargar archivo
          </Button>
          {plantilla}
        </div>
      </CardContent>
    </Card>
  );
}
