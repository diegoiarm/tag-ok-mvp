import { useState } from "react";
import { Loader2, Plus, Save, Trash2, TriangleAlert } from "lucide-react";
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
import { Skeleton } from "@/components/ui/skeleton";
import {
  usePorticoTarifa,
  useTramoTarifa,
  useUpdatePorticoTarifa,
  useUpdateTramoTarifa,
} from "@/hooks/useTarifas";
import { tipoVehiculoLabel } from "@/features/admin/lib/vehiculo";
import {
  TIPOS_DIA,
  TIPOS_TARIFA,
  TIPOS_VEHICULO,
  tipoDiaLabel,
  tipoTarifaLabel,
  toTarifaConfigInput,
} from "@/features/admin/lib/tarifa";
import {
  TipoDia,
  TipoTarifa,
  type TarifaConfigInput,
  type TipoVehiculo,
} from "@/types/types";

/** Identifica la entidad cuya tarifa se está editando. */
export type TarifaTarget = {
  tipo: "portico" | "tramo";
  id: number;
  titulo: string;
  subtitulo?: string;
};

interface Props {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  target: TarifaTarget | null;
}

export function TarifaFormSheet({ open, onOpenChange, target }: Props) {
  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-2xl flex flex-col gap-0 p-0">
        {open && target && (
          <Contenido target={target} onClose={() => onOpenChange(false)} />
        )}
      </SheetContent>
    </Sheet>
  );
}

function Contenido({
  target,
  onClose,
}: {
  target: TarifaTarget;
  onClose: () => void;
}) {
  const esPortico = target.tipo === "portico";
  const porticoQuery = usePorticoTarifa(esPortico ? target.id : null);
  const tramoQuery = useTramoTarifa(esPortico ? null : target.id);
  const query = esPortico ? porticoQuery : tramoQuery;

  const updatePortico = useUpdatePorticoTarifa();
  const updateTramo = useUpdateTramoTarifa();
  const guardando = updatePortico.isPending || updateTramo.isPending;

  const guardar = async (input: TarifaConfigInput) => {
    if (esPortico) {
      await updatePortico.mutateAsync({ id: target.id, input });
    } else {
      await updateTramo.mutateAsync({ id: target.id, input });
    }
    onClose();
  };

  return (
    <>
      <SheetHeader className="border-b p-4">
        <SheetTitle>Editar tarifas — {target.titulo}</SheetTitle>
        <SheetDescription>
          {target.subtitulo ??
            "Define los valores por vehículo y los rangos horarios de cobro."}
        </SheetDescription>
      </SheetHeader>

      {query.isLoading ? (
        <div className="flex-1 space-y-3 p-4">
          {Array.from({ length: 5 }).map((_, i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
      ) : query.isError ? (
        <div className="flex flex-1 flex-col items-center justify-center gap-2 p-8 text-center">
          <TriangleAlert className="h-6 w-6 text-destructive" />
          <p className="text-sm text-muted-foreground">
            No se pudo cargar la configuración tarifaria.
          </p>
          <Button variant="outline" size="sm" onClick={() => query.refetch()}>
            Reintentar
          </Button>
        </div>
      ) : (
        <Formulario
          key={`${target.tipo}-${target.id}`}
          inicial={toTarifaConfigInput(query.data)}
          guardando={guardando}
          onGuardar={guardar}
          onCancelar={onClose}
        />
      )}
    </>
  );
}

/* ===================== Estado interno del formulario ===================== */

type ReglaState = {
  aplicaA: TipoVehiculo[];
  valores: Record<TipoTarifa, string>;
};

type TramoState = { inicio: string; fin: string };

type ReglaTemporalState = {
  tipoDia: TipoDia;
  tipoTarifa: TipoTarifa;
  tramos: TramoState[];
};

type FormState = {
  reglas: ReglaState[];
  calendario: ReglaTemporalState[];
};

function valoresVacios(): Record<TipoTarifa, string> {
  return { [TipoTarifa.TBFP]: "", [TipoTarifa.TBP]: "", [TipoTarifa.TS]: "" };
}

function estadoInicial(inicial: TarifaConfigInput): FormState {
  return {
    reglas: inicial.reglas.map((r) => {
      const valores = valoresVacios();
      r.valores.forEach((v) => {
        valores[v.tipoTarifa] = String(v.valor);
      });
      return { aplicaA: [...r.aplicaA], valores };
    }),
    calendario: inicial.calendario.reglas.map((r) => ({
      tipoDia: r.tipoDia,
      tipoTarifa: r.tipoTarifa,
      tramos: r.tramos.map((t) => ({ inicio: t.inicio, fin: t.fin })),
    })),
  };
}

function reglaNueva(): ReglaState {
  return { aplicaA: [], valores: valoresVacios() };
}

function reglaTemporalNueva(): ReglaTemporalState {
  return {
    tipoDia: TipoDia.LABORAL,
    tipoTarifa: TipoTarifa.TBFP,
    tramos: [{ inicio: "", fin: "" }],
  };
}

function Formulario({
  inicial,
  guardando,
  onGuardar,
  onCancelar,
}: {
  inicial: TarifaConfigInput;
  guardando: boolean;
  onGuardar: (input: TarifaConfigInput) => Promise<void>;
  onCancelar: () => void;
}) {
  const [form, setForm] = useState<FormState>(() => estadoInicial(inicial));
  const [error, setError] = useState<string | null>(null);

  /* ---- mutadores de reglas tarifarias ---- */
  const setReglas = (reglas: ReglaState[]) =>
    setForm((p) => ({ ...p, reglas }));

  const toggleVehiculo = (idx: number, v: TipoVehiculo) =>
    setReglas(
      form.reglas.map((r, i) => {
        if (i !== idx) return r;
        const aplicaA = r.aplicaA.includes(v)
          ? r.aplicaA.filter((x) => x !== v)
          : [...r.aplicaA, v];
        return { ...r, aplicaA };
      }),
    );

  const setValor = (idx: number, tipo: TipoTarifa, valor: string) =>
    setReglas(
      form.reglas.map((r, i) =>
        i === idx ? { ...r, valores: { ...r.valores, [tipo]: valor } } : r,
      ),
    );

  /* ---- mutadores de calendario ---- */
  const setCalendario = (calendario: ReglaTemporalState[]) =>
    setForm((p) => ({ ...p, calendario }));

  const setReglaTemporal = (idx: number, patch: Partial<ReglaTemporalState>) =>
    setCalendario(
      form.calendario.map((r, i) => (i === idx ? { ...r, ...patch } : r)),
    );

  const setTramo = (
    reglaIdx: number,
    tramoIdx: number,
    patch: Partial<TramoState>,
  ) =>
    setCalendario(
      form.calendario.map((r, i) => {
        if (i !== reglaIdx) return r;
        return {
          ...r,
          tramos: r.tramos.map((t, j) =>
            j === tramoIdx ? { ...t, ...patch } : t,
          ),
        };
      }),
    );

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (form.reglas.length === 0) {
      setError("Debes definir al menos una regla tarifaria.");
      return;
    }
    for (const r of form.reglas) {
      if (r.aplicaA.length === 0) {
        setError("Cada regla tarifaria debe aplicar a al menos un vehículo.");
        return;
      }
      for (const tipo of TIPOS_TARIFA) {
        const n = Number(r.valores[tipo]);
        if (r.valores[tipo] === "" || !Number.isFinite(n) || n < 0) {
          setError(
            `El valor de ${tipoTarifaLabel(tipo)} debe ser un número ≥ 0.`,
          );
          return;
        }
      }
    }
    if (form.calendario.length === 0) {
      setError("Debes definir al menos una regla de calendario.");
      return;
    }
    for (const r of form.calendario) {
      if (r.tramos.length === 0) {
        setError("Cada regla de calendario debe tener al menos un rango horario.");
        return;
      }
      for (const t of r.tramos) {
        if (!t.inicio || !t.fin) {
          setError("Todo rango horario debe tener hora de inicio y de fin.");
          return;
        }
        if (t.inicio >= t.fin) {
          setError(
            `La hora de inicio (${t.inicio}) debe ser anterior a la de fin (${t.fin}).`,
          );
          return;
        }
      }
    }

    const input: TarifaConfigInput = {
      reglas: form.reglas.map((r) => ({
        aplicaA: r.aplicaA,
        valores: TIPOS_TARIFA.map((tipo) => ({
          tipoTarifa: tipo,
          valor: Number(r.valores[tipo]),
        })),
      })),
      calendario: {
        reglas: form.calendario.map((r) => ({
          tipoTarifa: r.tipoTarifa,
          tipoDia: r.tipoDia,
          tramos: r.tramos.map((t) => ({ inicio: t.inicio, fin: t.fin })),
        })),
      },
    };

    setError(null);
    try {
      await onGuardar(input);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "No se pudo guardar la tarifa.",
      );
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-1 flex-col overflow-hidden">
      <div className="flex-1 overflow-y-auto p-4 space-y-6">
        {/* ---- Reglas tarifarias ---- */}
        <section className="space-y-3">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-semibold">Reglas tarifarias</h3>
              <p className="text-xs text-muted-foreground">
                Valor por tipo de vehículo y franja de tarifa.
              </p>
            </div>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => setReglas([...form.reglas, reglaNueva()])}
              disabled={guardando}
            >
              <Plus className="h-4 w-4" />
              Añadir regla
            </Button>
          </div>

          {form.reglas.length === 0 && (
            <p className="text-xs text-muted-foreground italic">
              Sin reglas. Añade una para asignar valores por vehículo.
            </p>
          )}

          {form.reglas.map((regla, idx) => (
            <div key={idx} className="rounded-md border p-3 space-y-3">
              <div className="flex items-start justify-between gap-2">
                <Label className="text-xs">Aplica a</Label>
                <Button
                  type="button"
                  variant="ghost"
                  size="icon-sm"
                  onClick={() =>
                    setReglas(form.reglas.filter((_, i) => i !== idx))
                  }
                  disabled={guardando}
                  title="Quitar regla"
                >
                  <Trash2 className="h-4 w-4 text-destructive" />
                </Button>
              </div>
              <div className="flex flex-wrap gap-1.5">
                {TIPOS_VEHICULO.map((v) => {
                  const activo = regla.aplicaA.includes(v);
                  return (
                    <Button
                      key={v}
                      type="button"
                      variant={activo ? "default" : "outline"}
                      size="sm"
                      className="h-7 text-xs"
                      onClick={() => toggleVehiculo(idx, v)}
                      disabled={guardando}
                    >
                      {tipoVehiculoLabel(v)}
                    </Button>
                  );
                })}
              </div>
              <div className="grid grid-cols-3 gap-2">
                {TIPOS_TARIFA.map((tipo) => (
                  <div key={tipo} className="space-y-1">
                    <Label className="text-[11px] text-muted-foreground">
                      {tipoTarifaLabel(tipo)}
                    </Label>
                    <Input
                      value={regla.valores[tipo]}
                      onChange={(e) => setValor(idx, tipo, e.target.value)}
                      placeholder="0"
                      inputMode="decimal"
                      className="font-mono h-8"
                      disabled={guardando}
                    />
                  </div>
                ))}
              </div>
            </div>
          ))}
        </section>

        {/* ---- Calendario ---- */}
        <section className="space-y-3">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-semibold">Calendario horario</h3>
              <p className="text-xs text-muted-foreground">
                Qué franja de tarifa aplica según día y horario.
              </p>
            </div>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() =>
                setCalendario([...form.calendario, reglaTemporalNueva()])
              }
              disabled={guardando}
            >
              <Plus className="h-4 w-4" />
              Añadir franja
            </Button>
          </div>

          {form.calendario.length === 0 && (
            <p className="text-xs text-muted-foreground italic">
              Sin franjas horarias definidas.
            </p>
          )}

          {form.calendario.map((regla, idx) => (
            <div key={idx} className="rounded-md border p-3 space-y-3">
              <div className="flex items-center gap-2">
                <Select
                  value={regla.tipoDia}
                  onValueChange={(v) =>
                    setReglaTemporal(idx, { tipoDia: v as TipoDia })
                  }
                  disabled={guardando}
                >
                  <SelectTrigger className="h-8 flex-1">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {TIPOS_DIA.map((d) => (
                      <SelectItem key={d} value={d}>
                        {tipoDiaLabel(d)}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Select
                  value={regla.tipoTarifa}
                  onValueChange={(v) =>
                    setReglaTemporal(idx, { tipoTarifa: v as TipoTarifa })
                  }
                  disabled={guardando}
                >
                  <SelectTrigger className="h-8 flex-1">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {TIPOS_TARIFA.map((t) => (
                      <SelectItem key={t} value={t}>
                        {tipoTarifaLabel(t)}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Button
                  type="button"
                  variant="ghost"
                  size="icon-sm"
                  onClick={() =>
                    setCalendario(form.calendario.filter((_, i) => i !== idx))
                  }
                  disabled={guardando}
                  title="Quitar franja"
                >
                  <Trash2 className="h-4 w-4 text-destructive" />
                </Button>
              </div>

              <div className="space-y-2">
                {regla.tramos.map((tramo, ti) => (
                  <div key={ti} className="flex items-center gap-2">
                    <Input
                      type="time"
                      value={tramo.inicio}
                      onChange={(e) =>
                        setTramo(idx, ti, { inicio: e.target.value })
                      }
                      className="h-8 font-mono"
                      disabled={guardando}
                    />
                    <span className="text-xs text-muted-foreground">a</span>
                    <Input
                      type="time"
                      value={tramo.fin}
                      onChange={(e) =>
                        setTramo(idx, ti, { fin: e.target.value })
                      }
                      className="h-8 font-mono"
                      disabled={guardando}
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon-sm"
                      onClick={() =>
                        setReglaTemporal(idx, {
                          tramos: regla.tramos.filter((_, j) => j !== ti),
                        })
                      }
                      disabled={guardando}
                      title="Quitar rango"
                    >
                      <Trash2 className="h-3.5 w-3.5 text-muted-foreground" />
                    </Button>
                  </div>
                ))}
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="h-7 text-xs"
                  onClick={() =>
                    setReglaTemporal(idx, {
                      tramos: [...regla.tramos, { inicio: "", fin: "" }],
                    })
                  }
                  disabled={guardando}
                >
                  <Plus className="h-3.5 w-3.5" />
                  Añadir rango
                </Button>
              </div>
            </div>
          ))}
        </section>

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
              Guardar tarifas
            </>
          )}
        </Button>
      </SheetFooter>
    </form>
  );
}
