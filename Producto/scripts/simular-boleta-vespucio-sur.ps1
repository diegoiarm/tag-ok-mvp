<#
.SYNOPSIS
    Carga al historial (vía Kafka) los cruces de la boleta real de Vespucio Sur N° 138441468,
    para probar la comparación con IA pasándole siempre el PDF/foto de la boleta real.

.DESCRIPTION
    Por defecto carga los 19 cruces EXACTOS de la boleta -> la comparación debe dar "todo cuadra".

    Con -ConDiferencias, lo cargado al historial varía respecto de la boleta real en 3 puntos,
    así la comparación contra el PDF real debe reportar:
        * PdC3.2 del 02/03 16:15 registrado con $372,30 en vez de $472,30 -> MONTO_DIFERENTE
        * el cruce PdC5.3 del 12/03 19:05 ($1.655,05) NO se carga              -> SOLO_EN_FACTURA
        * se agrega un cruce PdC2.1 del 12/03 19:01 ($425,00) inexistente      -> SOLO_EN_APP

    Siempre borra el historial previo del usuario antes de cargar (para poder alternar
    entre modo exacto y modo con diferencias sin duplicar cruces).

    En la app: Boleta -> patente JHGK50, desde 15-02-2026 hasta 16-03-2026 -> generar
    -> "Verificar factura con IA" -> adjuntar el PDF (o foto) de la boleta real.

.EXAMPLE
    .\simular-boleta-vespucio-sur.ps1                       # historial idéntico a la boleta
    .\simular-boleta-vespucio-sur.ps1 -ConDiferencias       # historial con 3 discrepancias
    .\simular-boleta-vespucio-sur.ps1 -UsuarioId "<uuid>"   # para otra cuenta de Supabase
#>
param(
    # UUID de Supabase del usuario (claim `sub` del JWT). Default: cuenta tag.ok.mvp@gmail.com
    [string]$UsuarioId = "42c0afdb-aba1-44ec-8a25-ead43598c5d7",
    [string]$Patente = "JHGK50",
    [switch]$ConDiferencias
)

$ErrorActionPreference = "Stop"
$cultura = [System.Globalization.CultureInfo]::GetCultureInfo("es-CL")
$invariante = [System.Globalization.CultureInfo]::InvariantCulture

# ── Transacciones exactas de la boleta 138441468 (Vespucio Sur) ─────────────────
# codigo | dd/MM/yyyy HH:mm | tipo tarifa | importe
$transacciones = @(
    @("PdC1.3", "16/02/2026 03:55", "TBFP",  532.59),
    @("PdC1.3", "16/02/2026 18:09", "TBFP",  532.59),
    @("PdC1.3", "20/02/2026 13:31", "TBFP",  532.59),
    @("PdC3.4", "02/03/2026 16:13", "TBFP",  120.59),
    @("PdC3.2", "02/03/2026 16:15", "TBFP",  472.30),
    @("PdC3.1", "02/03/2026 17:22", "TBFP",  332.62),
    @("PdC3.3", "02/03/2026 17:24", "TBFP",  260.27),
    @("PdC2.2", "09/03/2026 09:19", "TBFP",  251.22),
    @("PdC3.4", "09/03/2026 09:20", "TBFP",  120.59),
    @("PdC3.2", "09/03/2026 09:22", "TBFP",  472.30),
    @("PdC4.3", "09/03/2026 09:24", "TBP",    90.44),
    @("PdC4.2", "09/03/2026 09:26", "TBP",   532.59),
    @("PdC5.4", "09/03/2026 09:28", "TBP",   765.73),
    @("PdC5.2", "09/03/2026 09:29", "TBP",   580.83),
    @("PdC5.1", "12/03/2026 19:04", "TS",    364.78),
    @("PdC5.3", "12/03/2026 19:05", "TS",   1655.05),
    @("PdC4.1", "12/03/2026 19:08", "TBFP",  311.52),
    @("PdC3.1", "12/03/2026 19:12", "TBFP",  332.62),
    @("PdC3.3", "12/03/2026 19:14", "TBFP",  260.27)
)

# ── Variaciones (solo afectan lo que se carga al historial, nunca la boleta real) ─
if ($ConDiferencias)
{
    $alteradas = @()
    foreach ($t in $transacciones)
    {
        if ($t[0] -eq "PdC5.3" -and $t[1] -eq "12/03/2026 19:05") { continue }      # no se registra -> SOLO_EN_FACTURA
        if ($t[0] -eq "PdC3.2" -and $t[1] -eq "02/03/2026 16:15")
        {
            $alteradas += ,@($t[0], $t[1], $t[2], 372.30)                           # -100 -> MONTO_DIFERENTE
            continue
        }
        $alteradas += ,$t
    }
    $alteradas += ,@("PdC2.1", "12/03/2026 19:01", "TS", 425.00)                    # cruce fantasma -> SOLO_EN_APP
    $transacciones = $alteradas
}

# Siempre limpiar: alternar entre modos sin duplicar cruces
Write-Host "Borrando historial previo de $UsuarioId en Mongo..." -ForegroundColor Yellow
docker exec db-historial mongosh -u admin -p admin --authenticationDatabase admin historial_db --quiet `
    --eval "printjson(db.historial_anual.deleteMany({usuarioId: '$UsuarioId'}))"

# ── Evento Kafka ─────────────────────────────────────────────────────────────────
$cruces = $transacciones | ForEach-Object {
    $momento = [datetime]::ParseExact($_[1], "dd/MM/yyyy HH:mm", $invariante)
    [ordered]@{
        codigo         = $_[0]
        nombre         = $_[0]
        autopista      = "Vespucio Sur"
        tipoTarifa     = $_[2]
        valor          = $_[3]
        tipoVehiculo   = "AUTO"
        patente        = $Patente
        horaFechaCruce = $momento.ToString("yyyy-MM-ddTHH:mm:ss")
    }
}
$total = 0; $transacciones | ForEach-Object { $total += $_[3] }

$evento = [ordered]@{
    eventoId        = [guid]::NewGuid().ToString()
    usuarioId       = $UsuarioId
    total           = $total
    cruces          = $cruces
    fechaGeneracion = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
} | ConvertTo-Json -Depth 5 -Compress

$modo = if ($ConDiferencias) { "CON DIFERENCIAS" } else { "exacto" }
Write-Host "Publicando $($cruces.Count) cruces (modo $modo, total `$$($total.ToString('N2', $cultura))) en portico-cruzado..." -ForegroundColor Cyan
# Vía archivo + docker cp: el pipe de PowerShell 5.1 antepone un BOM que rompe el JSON
$tmpEventos = Join-Path $env:TEMP "evento-boleta-vs.jsonl"
[System.IO.File]::WriteAllText($tmpEventos, $evento + "`n", [System.Text.UTF8Encoding]::new($false))
docker cp $tmpEventos kafka:/tmp/evento-boleta-vs.jsonl | Out-Null
docker exec kafka bash -c "kafka-console-producer --bootstrap-server localhost:9092 --topic portico-cruzado < /tmp/evento-boleta-vs.jsonl" | Out-Null
Remove-Item $tmpEventos -Force

Write-Host "`nHistorial cargado (usuario $UsuarioId, patente $Patente, 16/02 al 12/03 de 2026)." -ForegroundColor Green
if ($ConDiferencias)
{
    Write-Host "Al comparar contra la boleta real debe reportar:" -ForegroundColor Green
    Write-Host "  - PdC3.2 02/03 16:15 -> MONTO_DIFERENTE (app `$372,30 vs boleta `$472,30)"
    Write-Host "  - PdC5.3 12/03 19:05 -> SOLO_EN_FACTURA (`$1.655,05)"
    Write-Host "  - PdC2.1 12/03 19:01 -> SOLO_EN_APP (`$425,00)"
}
else
{
    Write-Host "Al comparar contra la boleta real debe dar: todo cuadra." -ForegroundColor Green
}
Write-Host "`nEn la app: Boleta -> patente $Patente, desde 15-02-2026 hasta 16-03-2026 -> Verificar factura con IA -> adjuntar el PDF/foto de la boleta real." -ForegroundColor DarkGray
