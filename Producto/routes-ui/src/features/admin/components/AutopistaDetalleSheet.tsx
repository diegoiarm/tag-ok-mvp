import { useEffect, useMemo } from "react";
import { MapContainer, Marker, TileLayer, Tooltip, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import iconUrl from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import {
  ArrowRight,
  Download,
  MapPin,
  MapPinned,
  Route as RouteIcon,
} from "lucide-react";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import type {
  AutopistaPortico,
  AutopistaResumen,
  AutopistaTramo,
} from "@/types/types";

const porticoIcon = L.icon({
  iconUrl,
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
});

interface Props {
  autopista: AutopistaResumen | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onExport: (autopista: AutopistaResumen) => void;
}

function tieneCoords(p: AutopistaPortico): boolean {
  return (
    Number.isFinite(p.latitud) &&
    Number.isFinite(p.longitud) &&
    p.latitud !== 0 &&
    p.longitud !== 0
  );
}

export function AutopistaDetalleSheet({
  autopista,
  open,
  onOpenChange,
  onExport,
}: Props) {
  const puntos = useMemo<[number, number][]>(() => {
    if (!autopista) return [];
    return autopista.porticos
      .filter(tieneCoords)
      .map((p) => [p.latitud, p.longitud] as [number, number]);
  }, [autopista]);

  // Mapa id -> pórtico para resolver entrada/salida de los tramos (el backend
  // solo entrega el id en cada extremo, no el código ni el nombre).
  const porticoPorId = useMemo(() => {
    const map = new Map<number, AutopistaPortico>();
    autopista?.porticos.forEach((p) => map.set(p.id, p));
    return map;
  }, [autopista]);

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-md flex flex-col gap-0 p-0">
        {autopista && (
          <>
            <SheetHeader className="border-b p-4">
              <SheetTitle className="flex items-center gap-2">
                {autopista.nombre}
              </SheetTitle>
              <SheetDescription className="flex flex-wrap items-center gap-2">
                <span className="font-mono">{autopista.codigo}</span>
                <Badge variant="outline" className="gap-1">
                  {autopista.tipoCobro === "PORTICO" ? (
                    <MapPinned className="h-3 w-3" />
                  ) : (
                    <RouteIcon className="h-3 w-3" />
                  )}
                  {autopista.tipoCobro === "PORTICO" ? "Por pórtico" : "Por tramo"}
                </Badge>
              </SheetDescription>
            </SheetHeader>

            <div className="flex-1 overflow-y-auto">
              <div className="grid grid-cols-2 gap-px bg-border">
                <Metric label="Pórticos" value={autopista.totalPorticos} />
                <Metric label="Tramos" value={autopista.totalTramos} />
              </div>

              {puntos.length > 0 ? (
                <div className="h-56 w-full border-b">
                  <MapContainer
                    center={puntos[0]}
                    zoom={13}
                    scrollWheelZoom={false}
                    style={{ height: "100%", width: "100%" }}
                  >
                    <TileLayer
                      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                      attribution="&copy; OpenStreetMap"
                    />
                    {autopista.porticos.filter(tieneCoords).map((p) => (
                      <Marker
                        key={p.id}
                        position={[p.latitud, p.longitud]}
                        icon={porticoIcon}
                      >
                        <Tooltip>
                          <span className="font-mono">{p.codigo}</span> — {p.nombre}
                        </Tooltip>
                      </Marker>
                    ))}
                    <FitBounds points={puntos} />
                  </MapContainer>
                </div>
              ) : (
                <div className="flex items-center justify-center gap-2 h-32 border-b text-sm text-muted-foreground">
                  <MapPin className="h-4 w-4" />
                  Sin coordenadas para mostrar en el mapa
                </div>
              )}

              <div className="p-4">
                <h3 className="text-xs font-medium uppercase tracking-wider text-muted-foreground mb-3">
                  Pórticos ({autopista.porticos.length})
                </h3>
                {autopista.porticos.length === 0 ? (
                  <p className="text-sm text-muted-foreground">
                    Esta autopista no tiene pórticos registrados.
                  </p>
                ) : (
                  <ul className="space-y-1.5">
                    {autopista.porticos.map((p) => (
                      <PorticoRow key={p.id} portico={p} />
                    ))}
                  </ul>
                )}
              </div>

              {autopista.tramos.length > 0 && (
                <div className="border-t p-4">
                  <h3 className="text-xs font-medium uppercase tracking-wider text-muted-foreground mb-3">
                    Tramos ({autopista.tramos.length})
                  </h3>
                  <ul className="space-y-1.5">
                    {autopista.tramos.map((t) => (
                      <TramoRow
                        key={t.id}
                        tramo={t}
                        porticoPorId={porticoPorId}
                      />
                    ))}
                  </ul>
                </div>
              )}
            </div>

            <SheetFooter className="border-t">
              <Button
                variant="outline"
                className="w-full"
                onClick={() => onExport(autopista)}
              >
                <Download className="h-4 w-4" />
                Exportar configuración (JSON)
              </Button>
              <p className="text-xs text-muted-foreground text-center">
                Respaldo del estado actual tal como lo entrega el servicio.
              </p>
            </SheetFooter>
          </>
        )}
      </SheetContent>
    </Sheet>
  );
}

function FitBounds({ points }: { points: [number, number][] }) {
  const map = useMap();
  useEffect(() => {
    // Espera a que termine la animación del sheet antes de recalcular el tamaño.
    const t = setTimeout(() => {
      map.invalidateSize();
      if (points.length === 1) {
        map.setView(points[0], 14);
      } else if (points.length > 1) {
        map.fitBounds(points, { padding: [24, 24] });
      }
    }, 250);
    return () => clearTimeout(t);
  }, [map, points]);
  return null;
}

function Metric({ label, value }: { label: string; value: number }) {
  return (
    <div className="bg-card px-4 py-3">
      <p className="text-xs text-muted-foreground uppercase tracking-wider">
        {label}
      </p>
      <p className="text-xl font-semibold mt-0.5">{value}</p>
    </div>
  );
}

function PorticoRow({ portico }: { portico: AutopistaPortico }) {
  const hayCoords = tieneCoords(portico);
  const nReglas = portico.reglas?.length ?? 0;
  return (
    <li className="flex items-start gap-3 rounded-md border border-border bg-card p-2.5">
      <div className="flex h-8 w-8 items-center justify-center rounded-md bg-brand-soft text-brand shrink-0">
        <MapPin className="h-4 w-4" />
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2">
          <span className="text-xs font-mono text-muted-foreground">
            {portico.codigo}
          </span>
          {portico.sentido && (
            <Badge variant="secondary" className="text-[10px] px-1.5 py-0">
              {portico.sentido}
            </Badge>
          )}
        </div>
        <p className="text-sm font-medium truncate">{portico.nombre}</p>
        <p className="text-xs text-muted-foreground">
          {hayCoords
            ? `${portico.latitud.toFixed(5)}, ${portico.longitud.toFixed(5)}`
            : "Sin coordenadas"}
          {nReglas > 0 && ` · ${nReglas} regla${nReglas > 1 ? "s" : ""} tarifaria${nReglas > 1 ? "s" : ""}`}
        </p>
      </div>
    </li>
  );
}

function nombreExtremo(
  id: number | undefined,
  porticoPorId: Map<number, AutopistaPortico>,
): string {
  if (id == null) return "—";
  const portico = porticoPorId.get(id);
  if (!portico) return `#${id}`;
  return portico.nombre || portico.codigo || `#${id}`;
}

function TramoRow({
  tramo,
  porticoPorId,
}: {
  tramo: AutopistaTramo;
  porticoPorId: Map<number, AutopistaPortico>;
}) {
  const entrada = nombreExtremo(tramo.entrada?.id, porticoPorId);
  const salida = nombreExtremo(tramo.salida?.id, porticoPorId);
  const nReglas = tramo.reglas?.length ?? 0;
  return (
    <li className="flex items-start gap-3 rounded-md border border-border bg-card p-2.5">
      <div className="flex h-8 w-8 items-center justify-center rounded-md bg-muted text-muted-foreground shrink-0">
        <RouteIcon className="h-4 w-4" />
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-1.5 text-sm font-medium min-w-0">
          <span className="truncate">{entrada}</span>
          <ArrowRight className="h-3.5 w-3.5 text-muted-foreground shrink-0" />
          <span className="truncate">{salida}</span>
        </div>
        <p className="text-xs text-muted-foreground">
          {tramo.distanciaKm != null && `${tramo.distanciaKm.toFixed(2)} km`}
          {tramo.distanciaKm != null && nReglas > 0 && " · "}
          {nReglas > 0 &&
            `${nReglas} regla${nReglas > 1 ? "s" : ""} tarifaria${nReglas > 1 ? "s" : ""}`}
        </p>
      </div>
    </li>
  );
}
