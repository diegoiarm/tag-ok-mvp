// Mapa.tsx
import { useMemo, useState } from "react";
import { MapContainer, TileLayer, GeoJSON, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import type { GeoJsonObject } from "geojson";
import { Filter, MapPin, RefreshCw, Search, X } from "lucide-react";
import type { Coord, PorticoAdmin, PorticoResumen } from "../types/types";
import { useRoute } from "../hooks/useRoute";
import { usePorticosAdmin } from "../hooks/usePorticos";
import { PorticoMark } from "../components/PorticoMark";
import { CobroMark } from "../components/CobroMark";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

// Iconos por defecto
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";

const DefaultIcon = L.icon({
    iconUrl: icon,
    shadowUrl: iconShadow,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
});
L.Marker.prototype.options.icon = DefaultIcon;

const StartIcon = L.icon({
    iconUrl: "https://maps.google.com/mapfiles/ms/icons/green-dot.png",
    iconSize: [32, 32],
});

const EndIcon = L.icon({
    iconUrl: "https://maps.google.com/mapfiles/ms/icons/red-dot.png",
    iconSize: [32, 32],
});

type EstadoFiltro = "todos" | "activos" | "inactivos";
type TarifaFiltro = "todas" | "con" | "sin";

/** Adapta un pórtico admin a la forma que consume PorticoMark. */
function aResumen(p: PorticoAdmin): PorticoResumen {
    return {
        id: p.id,
        nombre: p.nombre,
        latitud: p.latitud,
        longitud: p.longitud,
        tieneTarifa: !!p.tieneTarifa,
    };
}

export function Mapa({ start, end }: { start: Coord; end: Coord }) {
    const { data: route, refetch: refetchRoute, isFetching: fetchingRoute } =
        useRoute(start, end, "AUTO");
    const {
        data: porticos,
        refetch: refetchPorticos,
        isFetching: fetchingPorticos,
    } = usePorticosAdmin();

    const [mostrarFiltros, setMostrarFiltros] = useState(false);
    const [busqueda, setBusqueda] = useState("");
    const [autopista, setAutopista] = useState<string>("todas");
    const [estado, setEstado] = useState<EstadoFiltro>("todos");
    const [tarifa, setTarifa] = useState<TarifaFiltro>("todas");

    const refrescando = fetchingRoute || fetchingPorticos;
    const refrescar = () => {
        refetchPorticos();
        refetchRoute();
    };

    // Opciones de autopista para el filtro (nombres únicos).
    const autopistasOpciones = useMemo(() => {
        const set = new Set<string>();
        porticos?.forEach((p) => {
            if (p.autopistaNombre) set.add(p.autopistaNombre);
        });
        return Array.from(set).sort((a, b) => a.localeCompare(b, "es"));
    }, [porticos]);

    // 1. Obtener lista de cobros desde route.cobros (polimórfica)
    const cobros = route?.cobros;

    // 2. IDs de pórticos que ya aparecen como cobros en la ruta calculada
    const usedPorticoIds = useMemo(() => {
        if (!cobros) return new Set<number>();
        const ids = new Set<number>();
        cobros.forEach((cobro) => {
            if ("porticoId" in cobro) {
                ids.add(cobro.porticoId);
            } else {
                ids.add(cobro.entradaId);
                ids.add(cobro.salidaId);
            }
        });
        return ids;
    }, [cobros]);

    // 3. Pórticos filtrados según los controles del panel.
    const porticosFiltrados = useMemo(() => {
        const q = busqueda.trim().toLowerCase();
        return (porticos ?? []).filter((p) => {
            if (autopista !== "todas" && p.autopistaNombre !== autopista)
                return false;
            if (estado === "activos" && !p.activo) return false;
            if (estado === "inactivos" && p.activo) return false;
            if (tarifa === "con" && !p.tieneTarifa) return false;
            if (tarifa === "sin" && p.tieneTarifa) return false;
            if (q) {
                const inNombre = p.nombre?.toLowerCase().includes(q);
                const inCodigo = p.codigo?.toLowerCase().includes(q);
                if (!inNombre && !inCodigo) return false;
            }
            return true;
        });
    }, [porticos, autopista, estado, tarifa, busqueda]);

    // 4. Pórticos visibles en el mapa que NO están en la ruta calculada.
    const visiblePorticos = useMemo(
        () =>
            porticosFiltrados
                .filter((p) => !usedPorticoIds.has(p.id))
                .map(aResumen),
        [porticosFiltrados, usedPorticoIds],
    );

    const hayFiltrosActivos =
        busqueda.trim() !== "" ||
        autopista !== "todas" ||
        estado !== "todos" ||
        tarifa !== "todas";

    const limpiarFiltros = () => {
        setBusqueda("");
        setAutopista("todas");
        setEstado("todos");
        setTarifa("todas");
    };

    // 5. Geometría combinada de la ruta, derivada de la respuesta del backend.
    const geoJsonData = useMemo<GeoJsonObject | null>(() => {
        if (!route) return null;

        if (route.mergedRouteGeometry) {
            try {
                return JSON.parse(route.mergedRouteGeometry) as GeoJsonObject;
            } catch (err) {
                console.error("Error al parsear mergedRouteGeometry", err);
            }
        }

        const segments = (route as { segments?: { geometry: string }[] }).segments;
        if (!segments || segments.length === 0) return null;

        const allCoords: [number, number][] = [];

        segments.forEach((seg) => {
            try {
                const geom = JSON.parse(seg.geometry);
                if (geom.type === "LineString") {
                    allCoords.push(...geom.coordinates);
                } else if (geom.type === "MultiLineString") {
                    geom.coordinates.forEach((line: [number, number][]) => {
                        allCoords.push(...line);
                    });
                }
            } catch (e) {
                console.error("Error parseando geometría:", e);
            }
        });

        if (allCoords.length === 0) return null;

        return {
            type: "FeatureCollection",
            features: [
                {
                    type: "Feature",
                    properties: {},
                    geometry: {
                        type: "LineString",
                        coordinates: allCoords,
                    },
                },
            ],
        } as GeoJsonObject;
    }, [route]);

    return (
        <div
            style={{
                position: "relative",
                // Alto disponible bajo el header del layout (h-12 = 3rem). Evita
                // el overflow que ocultaba los controles de zoom y desplazaba el
                // panel al abrir los selects (compensación de scrollbar de Radix).
                height: "calc(100vh - 3rem)",
                width: "100%",
                overflow: "hidden",
                isolation: "isolate",
            }}
        >
            {/* Controles superpuestos: refresh + filtros */}
            <div className="absolute right-3 top-3 z-[1000] flex flex-col items-end gap-2">
                <div className="flex items-center gap-2">
                    <Button
                        variant={mostrarFiltros ? "default" : "secondary"}
                        size="sm"
                        className="relative shadow-md transition-all hover:shadow-lg active:scale-95"
                        onClick={() => setMostrarFiltros((v) => !v)}
                    >
                        <Filter className="h-4 w-4" />
                        Filtros
                        {/* Punto absoluto: no altera el ancho del botón. */}
                        {hayFiltrosActivos && (
                            <span className="absolute -top-1 -right-1 h-2.5 w-2.5 rounded-full bg-brand ring-2 ring-background animate-in zoom-in duration-200" />
                        )}
                    </Button>
                    <Button
                        variant="secondary"
                        size="sm"
                        className="shadow-md transition-all hover:shadow-lg active:scale-95"
                        onClick={refrescar}
                        disabled={refrescando}
                        title="Actualizar mapa"
                    >
                        <RefreshCw
                            className={`h-4 w-4 transition-transform ${refrescando ? "animate-spin" : ""}`}
                        />
                        Actualizar
                    </Button>
                </div>

                {mostrarFiltros && (
                    <div className="w-72 rounded-lg border bg-background/95 p-3 shadow-lg backdrop-blur space-y-3 animate-in fade-in slide-in-from-top-2 duration-200">
                        <div className="relative">
                            <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                            <Input
                                placeholder="Buscar código o nombre..."
                                value={busqueda}
                                onChange={(e) => setBusqueda(e.target.value)}
                                className="pl-8 h-9"
                            />
                        </div>

                        <Select value={autopista} onValueChange={setAutopista}>
                            <SelectTrigger className="h-9">
                                <SelectValue placeholder="Autopista" />
                            </SelectTrigger>
                            <SelectContent className="z-[1100]">
                                <SelectItem value="todas">Todas las autopistas</SelectItem>
                                {autopistasOpciones.map((nombre) => (
                                    <SelectItem key={nombre} value={nombre}>
                                        {nombre}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>

                        <div className="grid grid-cols-2 gap-2">
                            <Select
                                value={estado}
                                onValueChange={(v) => setEstado(v as EstadoFiltro)}
                            >
                                <SelectTrigger className="h-9">
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent className="z-[1100]">
                                    <SelectItem value="todos">Estado: todos</SelectItem>
                                    <SelectItem value="activos">Activos</SelectItem>
                                    <SelectItem value="inactivos">Inactivos</SelectItem>
                                </SelectContent>
                            </Select>

                            <Select
                                value={tarifa}
                                onValueChange={(v) => setTarifa(v as TarifaFiltro)}
                            >
                                <SelectTrigger className="h-9">
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent className="z-[1100]">
                                    <SelectItem value="todas">Tarifa: todas</SelectItem>
                                    <SelectItem value="con">Con tarifa</SelectItem>
                                    <SelectItem value="sin">Sin tarifa</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="flex items-center justify-between">
                            <Badge
                                key={visiblePorticos.length}
                                variant="secondary"
                                className="gap-1 animate-in zoom-in-95 duration-200"
                            >
                                <MapPin className="h-3 w-3" />
                                {visiblePorticos.length} pórticos
                            </Badge>
                            {hayFiltrosActivos && (
                                <Button
                                    variant="ghost"
                                    size="sm"
                                    className="h-7 transition-colors animate-in fade-in duration-200"
                                    onClick={limpiarFiltros}
                                >
                                    <X className="h-3.5 w-3.5" />
                                    Limpiar
                                </Button>
                            )}
                        </div>
                    </div>
                )}
            </div>

            <MapContainer
                center={[start.lat, start.lon]}
                zoom={12}
                style={{ height: "100%", width: "100%" }}
            >
                <TileLayer
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    attribution="&copy; OpenStreetMap contributors"
                />

                {/* Línea de la ruta */}
                {geoJsonData && (
                    <GeoJSON
                        data={geoJsonData}
                        style={{ color: "#007bff", weight: 5 }}
                    />
                )}

                {/* Marcador de inicio */}
                <Marker position={[start.lat, start.lon]} icon={StartIcon}>
                    <Popup>
                        <strong>Inicio</strong>
                        <br />
                        Hora:{" "}
                        {route
                            ? new Date(
                                  route.fechaHoraInicio,
                              ).toLocaleTimeString("es-CL")
                            : "Calculando..."}
                    </Popup>
                </Marker>

                {/* Marcador de destino */}
                <Marker position={[end.lat, end.lon]} icon={EndIcon}>
                    <Popup>
                        <strong>Destino</strong>
                        <br />
                        Hora llegada:{" "}
                        {route
                            ? new Date(route.fechaHoraFin).toLocaleTimeString(
                                  "es-CL",
                              )
                            : "Calculando..."}
                        <br />
                        Total:{" "}
                        {route
                            ? `$${route.totalCost.toLocaleString("es-CL")}`
                            : "..."}
                        <br />
                        Porticos cruzados: {cobros?.length}
                    </Popup>
                </Marker>

                {/* Renderizado polimórfico de cobros (pórtico o tramo) */}
                {cobros?.map((cobro, idx) => (
                    <CobroMark key={idx} cobro={cobro} />
                ))}

                {/* Pórticos generales que no están en la ruta */}
                {visiblePorticos.map((p) => (
                    <PorticoMark key={p.id} portico={p} />
                ))}
            </MapContainer>
        </div>
    );
}
