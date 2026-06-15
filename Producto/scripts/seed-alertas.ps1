<#
.SYNOPSIS
    Deja todo listo para probar las alertas de presupuesto (CU14/CU15): inicia sesion
    en Supabase, siembra cruces del mes actual y crea/actualiza un presupuesto Global
    que cruza los umbrales, todo para el MISMO usuario.

.DESCRIPTION
    1. Lee SUPABASE_URL y SUPABASE_ANON_KEY desde Producto/.env.
    2. Login email/password contra Supabase Auth -> obtiene access_token y user_id.
    3. (Opcional) Limpia el historial Mongo de ese usuario.
    4. Publica cruces 'portico-cruzado' a Kafka (history-service los consume a Mongo).
    5. Calcula el limite (la mitad del gasto sembrado si no se pasa -Limite) y hace
       upsert del presupuesto Global con alertas activas.
    6. Borra las notificaciones del mes de ese usuario para que la alerta vuelva a dispararse.

    Requiere: contenedores kafka y db-historial arriba, history-service (perfil local)
    consumiendo, y la migracion 20260614_alertas_presupuesto.sql ya aplicada.

.EXAMPLE
    .\seed-alertas.ps1 -Email tag.ok.mvp@gmail.com -Password "****"
    .\seed-alertas.ps1 -Email yo@correo.com -Password "****" -Limite 5000 -LimpiarHistorial
#>
param(
    [Parameter(Mandatory = $true)] [string]$Email,
    [Parameter(Mandatory = $true)] [string]$Password,
    # Limite mensual en CLP. 0 = automatico (la mitad del gasto sembrado, redondeado).
    [int]$Limite = 0,
    [int]$Umbral1 = 50,
    [int]$Umbral2 = 80,
    [int]$Dias = 5,
    [int]$CrucesPorDia = 4,
    [string]$Patente = "GHJK82",
    # Borra el historial Mongo del usuario antes de sembrar (evita acumular cruces viejos).
    [switch]$LimpiarHistorial
)

$ErrorActionPreference = "Stop"
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

# Falla claro si un contenedor requerido no esta corriendo (docker exec no lanza excepcion solo).
function Require-Container([string]$name)
{
    $corriendo = @(docker ps --format "{{.Names}}")
    if ($corriendo -notcontains $name)
    {
        throw "El contenedor '$name' no esta corriendo. Levanta la infra primero:`n" +
              "    cd Producto; docker compose up -d db-rutas db-historial zookeeper kafka kafka-setup"
    }
}

# ── 1. Leer .env ────────────────────────────────────────────────────────────────
$envPath = Join-Path $PSScriptRoot "..\.env"
if (-not (Test-Path $envPath)) { throw "No se encontro $envPath" }

function Get-EnvValue([string]$key)
{
    $line = Get-Content $envPath | Where-Object { $_ -match "^\s*$key=" } | Select-Object -First 1
    if (-not $line) { return $null }
    return ($line -split '=', 2)[1].Trim()
}

$supaUrl = (Get-EnvValue "SUPABASE_URL")
$anonKey = (Get-EnvValue "SUPABASE_ANON_KEY")
if (-not $supaUrl -or -not $anonKey) { throw "Faltan SUPABASE_URL o SUPABASE_ANON_KEY en .env" }
$supaUrl = $supaUrl.TrimEnd('/')

# ── 2. Login -> token + user_id ──────────────────────────────────────────────────
Write-Host "Iniciando sesion como $Email ..." -ForegroundColor Cyan
$loginBody = @{ email = $Email; password = $Password } | ConvertTo-Json
$auth = Invoke-RestMethod -Method Post `
    -Uri "$supaUrl/auth/v1/token?grant_type=password" `
    -Headers @{ apikey = $anonKey; "Content-Type" = "application/json" } `
    -Body $loginBody
$token  = $auth.access_token
$userId = $auth.user.id
if (-not $token -or -not $userId) { throw "Login fallido: no se obtuvo token/user_id" }
$restHeaders = @{ apikey = $anonKey; Authorization = "Bearer $token" }
Write-Host "  user_id = $userId" -ForegroundColor DarkGray

# ── 3. (Opcional) limpiar historial Mongo del usuario ────────────────────────────
if ($LimpiarHistorial)
{
    Require-Container "db-historial"
    Write-Host "Borrando historial Mongo previo de $userId ..." -ForegroundColor Yellow
    docker exec db-historial mongosh -u admin -p admin --authenticationDatabase admin historial_db --quiet `
        --eval "printjson(db.historial_anual.deleteMany({usuarioId: '$userId'}))"
}

# ── 4. Sembrar cruces del mes actual ─────────────────────────────────────────────
# Porticos reales de db_rutas (sin acentos para evitar el gotcha de encoding de PS 5.1).
$catalogo = @(
    @{ codigo = "P101"; nombre = "Bilbao";              autopista = "Vespucio Oriente 1"; valor = 620 },
    @{ codigo = "P103"; nombre = "Los Militares";       autopista = "Vespucio Oriente 1"; valor = 580 },
    @{ codigo = "P110"; nombre = "La Piramide";         autopista = "Vespucio Oriente 1"; valor = 830 },
    @{ codigo = "P205"; nombre = "Vitacura";            autopista = "Vespucio Oriente 1"; valor = 650 },
    @{ codigo = "1.1";  nombre = "General Velasquez";   autopista = "Vespucio Sur";       valor = 940 },
    @{ codigo = "3.1";  nombre = "Santa Rosa";          autopista = "Vespucio Sur";       valor = 1020 },
    @{ codigo = "5.1";  nombre = "Grecia - Quilin";     autopista = "Vespucio Sur";       valor = 760 }
)
$tiposTarifa = @("TBFP", "TBP", "TS")

$eventos = @()
$totalGastado = 0
$totalCruces = 0
for ($d = $Dias - 1; $d -ge 0; $d--)
{
    $fecha = (Get-Date).Date.AddDays(-$d)
    $cruces = @()
    $horas = 7..20 | Get-Random -Count $CrucesPorDia | Sort-Object
    foreach ($hora in $horas)
    {
        $p = $catalogo | Get-Random
        $momento = $fecha.AddHours($hora).AddMinutes((Get-Random -Minimum 0 -Maximum 60))
        $cruces += [ordered]@{
            codigo         = $p.codigo
            nombre         = $p.nombre
            autopista      = $p.autopista
            tipoTarifa     = $tiposTarifa | Get-Random
            valor          = $p.valor
            tipoVehiculo   = "AUTO"
            patente        = $Patente
            horaFechaCruce = $momento.ToString("yyyy-MM-ddTHH:mm:ss")
        }
        $totalGastado += $p.valor
        $totalCruces++
    }
    $totalDia = ($cruces | ForEach-Object { $_.valor } | Measure-Object -Sum).Sum
    $eventos += ([ordered]@{
        eventoId        = [guid]::NewGuid().ToString()
        usuarioId       = $userId
        total           = $totalDia
        cruces          = $cruces
        fechaGeneracion = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
    } | ConvertTo-Json -Depth 5 -Compress)
}

# El contenedor de kafka puede llamarse 'kafka' o 'kafka-tag' segun la version del compose.
$kafkaContainer = @(docker ps --format "{{.Names}}" | Where-Object { $_ -match 'kafka' -and $_ -notmatch 'setup' }) | Select-Object -First 1
if (-not $kafkaContainer)
{
    throw "No hay contenedor kafka corriendo. Levanta la infra:`n" +
          "    cd Producto; docker compose up -d zookeeper-tag kafka-tag kafka-setup-tag"
}
Write-Host "Publicando $($eventos.Count) eventos ($totalCruces cruces, total `$$totalGastado) en portico-cruzado (via $kafkaContainer)..." -ForegroundColor Cyan
# docker cp + redirect: el pipe de PS 5.1 antepone un BOM que rompe el parseo JSON del consumer.
$tmp = Join-Path $env:TEMP "seed-alertas.jsonl"
[System.IO.File]::WriteAllText($tmp, ($eventos -join "`n") + "`n", [System.Text.UTF8Encoding]::new($false))
docker cp $tmp "${kafkaContainer}:/tmp/seed-alertas.jsonl" | Out-Null
if ($LASTEXITCODE -ne 0) { Remove-Item $tmp -Force; throw "Fallo 'docker cp' al contenedor $kafkaContainer (exit $LASTEXITCODE)." }
docker exec $kafkaContainer bash -c "kafka-console-producer --bootstrap-server localhost:9092 --topic portico-cruzado < /tmp/seed-alertas.jsonl" | Out-Null
if ($LASTEXITCODE -ne 0) { Remove-Item $tmp -Force; throw "Fallo al publicar en Kafka (exit $LASTEXITCODE). Revisa que el contenedor $kafkaContainer este sano (docker ps)." }
Remove-Item $tmp -Force

# ── 5. Calcular limite y upsert del presupuesto Global ───────────────────────────
if ($Limite -le 0)
{
    $Limite = [math]::Max(1000, [int]([math]::Round(($totalGastado / 2.0) / 1000.0) * 1000))
}
if ($Umbral1 -ge $Umbral2) { throw "Umbral1 ($Umbral1) debe ser menor que Umbral2 ($Umbral2)" }

$existing = @(Invoke-RestMethod -Method Get `
    -Uri "$supaUrl/rest/v1/presupuesto?select=id&user_id=eq.$userId&vehiculo_id=is.null" `
    -Headers $restHeaders)

$writeHeaders = @{ apikey = $anonKey; Authorization = "Bearer $token"; "Content-Type" = "application/json"; Prefer = "return=minimal" }

if ($existing.Count -ge 1 -and $existing[0].id)
{
    $presId = $existing[0].id
    $body = @{ monto_mensual = $Limite; umbral_alerta_1 = $Umbral1; umbral_alerta_2 = $Umbral2; alertas_activas = $true } | ConvertTo-Json
    Invoke-RestMethod -Method Patch -Uri "$supaUrl/rest/v1/presupuesto?id=eq.$presId" -Headers $writeHeaders -Body $body | Out-Null
    Write-Host "Presupuesto Global actualizado (id $presId)." -ForegroundColor Green
}
else
{
    $body = @{ user_id = $userId; vehiculo_id = $null; monto_mensual = $Limite; umbral_alerta_1 = $Umbral1; umbral_alerta_2 = $Umbral2; alertas_activas = $true } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri "$supaUrl/rest/v1/presupuesto" -Headers $writeHeaders -Body $body | Out-Null
    Write-Host "Presupuesto Global creado." -ForegroundColor Green
}

# ── 6. Limpiar notificaciones del mes (para que la alerta vuelva a dispararse) ────
$periodo = (Get-Date).ToString("yyyy-MM")
Invoke-RestMethod -Method Delete `
    -Uri "$supaUrl/rest/v1/notificacion?user_id=eq.$userId&periodo=eq.$periodo" `
    -Headers @{ apikey = $anonKey; Authorization = "Bearer $token"; Prefer = "return=minimal" } | Out-Null

# ── Resumen ───────────────────────────────────────────────────────────────────────
$pct = [int]([math]::Round(($totalGastado / [double]$Limite) * 100))
Write-Host ""
Write-Host "===========================================================" -ForegroundColor White
Write-Host " Listo. Datos sembrados para $Email" -ForegroundColor Green
Write-Host "   Gasto del mes ($periodo):  `$$totalGastado  ($totalCruces cruces)"
Write-Host "   Limite Global:            `$$Limite"
Write-Host "   Umbrales:                 $Umbral1% / $Umbral2%"
Write-Host "   Gasto actual:             $pct%  ->  cruza $((@($Umbral1,$Umbral2) | Where-Object { $_ -le $pct }).Count) umbral(es)"
Write-Host "===========================================================" -ForegroundColor White
Write-Host "Abre la app con $Email y entra a Home: se dispararan las alertas + el badge." -ForegroundColor Cyan
