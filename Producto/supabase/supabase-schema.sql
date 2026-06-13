-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.vehiculos (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  patente text NOT NULL,
  tipo_vehiculo text NOT NULL,
  numero_tag text,
  alias text,
  es_principal boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT vehiculos_pkey PRIMARY KEY (id),
  CONSTRAINT vehiculos_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);

CREATE TABLE public.presupuesto (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  vehiculo_id uuid,
  monto_mensual integer NOT NULL CHECK (monto_mensual > 0),
  umbral_alerta_1 integer NOT NULL DEFAULT 75 CHECK (umbral_alerta_1 >= 1 AND umbral_alerta_1 <= 99),
  umbral_alerta_2 integer NOT NULL DEFAULT 90 CHECK (umbral_alerta_2 >= 1 AND umbral_alerta_2 <= 100),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT presupuesto_pkey PRIMARY KEY (id),
  CONSTRAINT presupuesto_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id),
  CONSTRAINT presupuesto_vehiculo_id_fkey FOREIGN KEY (vehiculo_id) REFERENCES public.vehiculos(id)
);