-- CU14 / CU15: alertas de presupuesto + historial de notificaciones (campanita).
-- Ejecutar en el SQL editor de Supabase (o vía CLI) una sola vez.

-- ── CU14: activar / desactivar alertas por presupuesto ──────────────────────────
ALTER TABLE public.presupuesto
  ADD COLUMN IF NOT EXISTS alertas_activas boolean NOT NULL DEFAULT true;

-- ── Historial de notificaciones (alimenta la campanita) ─────────────────────────
CREATE TABLE IF NOT EXISTS public.notificacion (
  id          uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id     uuid NOT NULL,
  vehiculo_id uuid,
  tipo        text NOT NULL DEFAULT 'PRESUPUESTO_UMBRAL',
  titulo      text NOT NULL,
  cuerpo      text NOT NULL,
  umbral      integer,            -- % de umbral que disparó la alerta
  porcentaje  integer,            -- % real de gasto al momento del disparo
  periodo     text,               -- 'YYYY-MM' del mes evaluado (para deduplicar)
  leida       boolean NOT NULL DEFAULT false,
  created_at  timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT notificacion_pkey PRIMARY KEY (id),
  CONSTRAINT notificacion_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id),
  CONSTRAINT notificacion_vehiculo_id_fkey FOREIGN KEY (vehiculo_id)
    REFERENCES public.vehiculos(id) ON DELETE CASCADE
);

-- Dedupe: una sola alerta por (usuario, vehículo/global, mes, umbral, tipo).
CREATE UNIQUE INDEX IF NOT EXISTS notificacion_dedupe_idx
  ON public.notificacion (
    user_id,
    COALESCE(vehiculo_id::text, 'GLOBAL'),
    periodo,
    umbral,
    tipo
  )
  WHERE periodo IS NOT NULL AND umbral IS NOT NULL;

CREATE INDEX IF NOT EXISTS notificacion_user_created_idx
  ON public.notificacion (user_id, created_at DESC);

-- ── RLS: cada usuario solo ve / escribe sus propias notificaciones ──────────────
ALTER TABLE public.notificacion ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS notificacion_select_own ON public.notificacion;
CREATE POLICY notificacion_select_own ON public.notificacion
  FOR SELECT USING (auth.uid() = user_id);

DROP POLICY IF EXISTS notificacion_insert_own ON public.notificacion;
CREATE POLICY notificacion_insert_own ON public.notificacion
  FOR INSERT WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS notificacion_update_own ON public.notificacion;
CREATE POLICY notificacion_update_own ON public.notificacion
  FOR UPDATE USING (auth.uid() = user_id);

DROP POLICY IF EXISTS notificacion_delete_own ON public.notificacion;
CREATE POLICY notificacion_delete_own ON public.notificacion
  FOR DELETE USING (auth.uid() = user_id);
