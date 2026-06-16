<#
.SYNOPSIS
    Verifica que los datos de la prueba de alertas esten almacenados: historial Mongo
    (gasto del mes), presupuesto en Supabase y notificaciones del periodo actual.

.DESCRIPTION
    Hace login con el mismo usuario y muestra, para el mes en curso:
      - el total y cruces del historial (history-service / Mongo),
      - los presupuestos del usuario (Supabase),
      - las notificaciones del periodo (Supabase) y cuantas estan sin leer.

    Corre esto DESPUES de seed-alertas.ps1 (para ver historial + presupuesto), y otra
    vez DESPUES de abrir Home en la app (para ver las notificaciones que genero el
    AlertaService).

.EXAMPLE
    .\verificar-alertas.ps1 -Email tag.ok.mvp@gmail.com -Password "****"
#>
param(
    [Parameter(Mandatory = $true)] [string]$Email,
    [Parameter(Mandatory = $true)] [string]$Password
)

$ErrorActionPreference = "Stop"
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$envPath = Join-Path $PSScriptRoot "..\.env"
if (-not (Test-Path $envPath)) { throw "No se encontro $envPath" }
function Get-EnvValue([string]$key)
{
    $line = Get-Content $envPath | Where-Object { $_ -match "^\s*$key=" } | Select-Object -First 1
    if (-not $line) { return $null }
    return ($line -split '=', 2)[1].Trim()
}
$supaUrl = (Get-EnvValue "SUPABASE_URL").TrimEnd('/')
$anonKey = (Get-EnvValue "SUPABASE_ANON_KEY")

# Login
$auth = Invoke-RestMethod -Method Post -Uri "$supaUrl/auth/v1/token?grant_type=password" `
    -Headers @{ apikey = $anonKey; "Content-Type" = "application/json" } `
    -Body (@{ email = $Email; password = $Password } | ConvertTo-Json)
$token  = $auth.access_token
$userId = $auth.user.id
$restHeaders = @{ apikey = $anonKey; Authorization = "Bearer $token" }
$periodo = (Get-Date).ToString("yyyy-MM")
$year    = (Get-Date).Year

Write-Host "Usuario: $Email" -ForegroundColor White
Write-Host "user_id: $userId" -ForegroundColor DarkGray
Write-Host ""

# ── 1. Historial Mongo (gasto del mes) ────────────────────────────────────────────
Write-Host "== 1. Historial (Mongo, anio $year) ==" -ForegroundColor Cyan
$mongoEval = "var d=db.historial_anual.findOne({_id:'$userId-$year'}); if(!d){print('(sin historial para este usuario/anio)')} else { print('cruces totales del anio: '+d.cantidadCruces); d.meses.forEach(function(m){ print('  mes '+m.mes+': total '+m.totalMes+'  ('+m.cantidadCruces+' cruces)'); }); }"
docker exec db-historial mongosh -u admin -p admin --authenticationDatabase admin historial_db --quiet --eval $mongoEval
Write-Host ""

# ── 2. Presupuestos (Supabase) ────────────────────────────────────────────────────
Write-Host "== 2. Presupuestos (Supabase) ==" -ForegroundColor Cyan
$pres = @(Invoke-RestMethod -Method Get -Headers $restHeaders `
    -Uri "$supaUrl/rest/v1/presupuesto?select=*&user_id=eq.$userId&order=created_at.asc")
if ($pres.Count -eq 0) { Write-Host "  (sin presupuestos)" -ForegroundColor Yellow }
foreach ($p in $pres)
{
    $alcance = if ($null -eq $p.vehiculo_id) { "Global" } else { "vehiculo " + $p.vehiculo_id }
    $estado  = if ($p.alertas_activas) { "ON" } else { "OFF" }
    Write-Host ("  [{0}] limite `${1}  umbrales {2}%/{3}%  alertas:{4}" -f `
        $alcance, $p.monto_mensual, $p.umbral_alerta_1, $p.umbral_alerta_2, $estado)
}
Write-Host ""

# ── 3. Notificaciones del periodo (Supabase) ──────────────────────────────────────
Write-Host "== 3. Notificaciones de $periodo (Supabase) ==" -ForegroundColor Cyan
$notifs = @(Invoke-RestMethod -Method Get -Headers $restHeaders `
    -Uri "$supaUrl/rest/v1/notificacion?select=*&user_id=eq.$userId&periodo=eq.$periodo&order=created_at.desc")
if ($notifs.Count -eq 0)
{
    Write-Host "  (sin notificaciones aun - abre Home en la app para que el AlertaService las genere)" -ForegroundColor Yellow
}
else
{
    $sinLeer = @($notifs | Where-Object { -not $_.leida }).Count
    Write-Host ("  {0} notificacion(es), {1} sin leer:" -f $notifs.Count, $sinLeer)
    foreach ($n in $notifs)
    {
        $marca = if ($n.leida) { "[leida]" } else { "[NUEVA]" }
        Write-Host ("    {0} {1}" -f $marca, $n.titulo)
        Write-Host ("            {0}" -f $n.cuerpo) -ForegroundColor DarkGray
    }
}
