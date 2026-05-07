-- Tabla de presupuestos mensuales por usuario
-- vehiculo_id = NULL  →  presupuesto global
-- vehiculo_id = <id>  →  presupuesto específico por vehículo

create table if not exists presupuesto (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid references auth.users(id) on delete cascade not null,
    vehiculo_id     uuid references vehiculos(id)  on delete set null,
    monto_mensual   int  not null check (monto_mensual > 0),
    umbral_alerta_1 int  not null default 75 check (umbral_alerta_1 between 1 and 99),
    umbral_alerta_2 int  not null default 90 check (umbral_alerta_2 between 1 and 100),
    created_at      timestamptz default now() not null
);

-- Un solo presupuesto global por usuario
create unique index if not exists presupuesto_global_uq
    on presupuesto(user_id) where vehiculo_id is null;

-- Un solo presupuesto por vehículo por usuario
create unique index if not exists presupuesto_vehiculo_uq
    on presupuesto(user_id, vehiculo_id) where vehiculo_id is not null;

-- Row-Level Security
alter table presupuesto enable row level security;

create policy "users_manage_own_presupuesto" on presupuesto
    for all
    using  (auth.uid() = user_id)
    with check (auth.uid() = user_id);
