<#
.SYNOPSIS
    Carga recorridos simulados al historial produciendo eventos `portico-cruzado` a Kafka.

.DESCRIPTION
    Genera cruces de pórticos reales (nombres tomados de db_rutas) para un usuario y patente,
    los publica en el topic `portico-cruzado` y deja que history-service los consuma hacia Mongo.
    Opcionalmente genera una factura HTML simulada con los mismos cruces para probar la
    comparación con IA (imprimir a PDF o fotografiarla desde la app).

    Requiere los contenedores `kafka` y `db-historial` corriendo, y history-service levantado
    (perfil local) para que consuma los eventos.

.EXAMPLE
    .\simular-cruces.ps1                                  # 5 días de cruces para el usuario default
    .\simular-cruces.ps1 -UsuarioId "<uuid>" -Limpiar     # borra el historial previo de ese usuario y recarga
    .\simular-cruces.ps1 -ConDiferencias                  # la factura HTML incluye discrepancias a propósito
#>
param(
    # UUID de Supabase del usuario (claim `sub` del JWT). Default: cuenta tag.ok.mvp@gmail.com
    [string]$UsuarioId = "42c0afdb-aba1-44ec-8a25-ead43598c5d7",
    [string]$Patente = "GHJK82",
    [int]$Dias = 5,
    [int]$CrucesPorDia = 3,
    # Borra el historial previo del usuario en Mongo antes de cargar (evita duplicados al re-ejecutar)
    [switch]$Limpiar,
    # La factura simulada incluye un monto distinto y un cruce extra (para probar discrepancias)
    [switch]$ConDiferencias
)

$ErrorActionPreference = "Stop"
# Los nombres de pórticos llevan acentos; sin esto el pipe a docker los corrompe
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)

# Pórticos reales de db_rutas (codigo | nombre | autopista | tarifa CLP aproximada)
$catalogo = @(
    @{ codigo = "P101"; nombre = "Bilbao";                        autopista = "Vespucio Oriente 1"; valor = 620 },
    @{ codigo = "P103"; nombre = "Los Militares";                 autopista = "Vespucio Oriente 1"; valor = 580 },
    @{ codigo = "P104"; nombre = "Presidente Riesco";             autopista = "Vespucio Oriente 1"; valor = 710 },
    @{ codigo = "P110"; nombre = "La Pirámide";                   autopista = "Vespucio Oriente 1"; valor = 830 },
    @{ codigo = "P205"; nombre = "Vitacura";                      autopista = "Vespucio Oriente 1"; valor = 650 },
    @{ codigo = "P211"; nombre = "Tobalaba";                      autopista = "Vespucio Oriente 1"; valor = 690 },
    @{ codigo = "1.1";  nombre = "General Velásquez - Ruta 78";   autopista = "Vespucio Sur";       valor = 940 },
    @{ codigo = "2.1";  nombre = "Ruta 5 - General Velásquez";    autopista = "Vespucio Sur";       valor = 880 },
    @{ codigo = "3.1";  nombre = "Santa Rosa - Gran Avenida";     autopista = "Vespucio Sur";       valor = 1020 },
    @{ codigo = "5.1";  nombre = "Grecia - Quilín";               autopista = "Vespucio Sur";       valor = 760 }
)
$tiposTarifa = @("TBFP", "TBP", "TS")

if ($Limpiar)
{
    Write-Host "Borrando historial previo de $UsuarioId en Mongo..." -ForegroundColor Yellow
    docker exec db-historial mongosh -u admin -p admin --authenticationDatabase admin historial_db --quiet `
        --eval "printjson(db.historial_anual.deleteMany({usuarioId: '$UsuarioId'}))"
}

# Un evento por día, con N cruces cada uno
$eventos = @()
$todosLosCruces = @()
for ($d = $Dias - 1; $d -ge 0; $d--)
{
    $fecha = (Get-Date).Date.AddDays(-$d)
    $cruces = @()
    $horas = 7..20 | Get-Random -Count $CrucesPorDia | Sort-Object

    foreach ($hora in $horas)
    {
        $p = $catalogo | Get-Random
        $momento = $fecha.AddHours($hora).AddMinutes((Get-Random -Minimum 0 -Maximum 60))
        $cruce = [ordered]@{
            codigo         = $p.codigo
            nombre         = $p.nombre
            autopista      = $p.autopista
            tipoTarifa     = $tiposTarifa | Get-Random
            valor          = $p.valor
            tipoVehiculo   = "AUTO"
            patente        = $Patente
            horaFechaCruce = $momento.ToString("yyyy-MM-ddTHH:mm:ss")
        }
        $cruces += $cruce
        $todosLosCruces += $cruce
    }

    $total = ($cruces | ForEach-Object { $_.valor } | Measure-Object -Sum).Sum
    $eventos += ([ordered]@{
        eventoId        = [guid]::NewGuid().ToString()
        usuarioId       = $UsuarioId
        total           = $total
        cruces          = $cruces
        fechaGeneracion = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
    } | ConvertTo-Json -Depth 5 -Compress)
}

Write-Host "Publicando $($eventos.Count) eventos ($($todosLosCruces.Count) cruces) en portico-cruzado..." -ForegroundColor Cyan
# Vía archivo + docker cp: el pipe de PowerShell 5.1 antepone un BOM (0xFEFF)
# al stream, lo que rompe el parseo JSON del primer evento en el consumer
$tmpEventos = Join-Path $env:TEMP "eventos-portico.jsonl"
[System.IO.File]::WriteAllText($tmpEventos, ($eventos -join "`n") + "`n", [System.Text.UTF8Encoding]::new($false))
docker cp $tmpEventos kafka:/tmp/eventos-portico.jsonl | Out-Null
docker exec kafka bash -c "kafka-console-producer --bootstrap-server localhost:9092 --topic portico-cruzado < /tmp/eventos-portico.jsonl" | Out-Null
Remove-Item $tmpEventos -Force

Write-Host "`nCruces enviados (usuario $UsuarioId, patente $Patente):" -ForegroundColor Green
$todosLosCruces | ForEach-Object {
    "{0}  {1,-32} {2,-20} `$ {3}" -f $_.horaFechaCruce, $_.nombre, $_.autopista, $_.valor
}

# ── Factura simulada del "cliente" para probar la comparación con IA ────────────
$crucesFactura = @($todosLosCruces | ForEach-Object {
    $copia = [ordered]@{}
    foreach ($k in $_.Keys) { $copia[$k] = $_[$k] }
    $copia
})
$notaDiferencias = ""
if ($ConDiferencias -and $crucesFactura.Count -ge 2)
{
    # Un monto inflado y un cruce que la app no tiene
    $crucesFactura[0].valor = $crucesFactura[0].valor + 500
    $extra = $catalogo | Get-Random
    $crucesFactura += [ordered]@{
        codigo = $extra.codigo; nombre = $extra.nombre; autopista = $extra.autopista
        tipoTarifa = "TS"; valor = $extra.valor; tipoVehiculo = "AUTO"; patente = $Patente
        horaFechaCruce = (Get-Date).Date.AddHours(23).ToString("yyyy-MM-ddTHH:mm:ss")
    }
    $notaDiferencias = " (con 1 monto alterado y 1 cobro extra)"
}

$filas = ($crucesFactura | ForEach-Object {
    $dt = [datetime]::Parse($_.horaFechaCruce)
    "<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td style='text-align:right'>`$ {4:N0}</td></tr>" -f `
        $dt.ToString("dd-MM-yyyy"), $dt.ToString("HH:mm"), $_.nombre, $_.autopista, $_.valor
}) -join "`n"
$totalFactura = ($crucesFactura | ForEach-Object { $_.valor } | Measure-Object -Sum).Sum

$html = @"
<!DOCTYPE html>
<html lang="es"><head><meta charset="utf-8"><title>Factura de peajes</title>
<style>
 body { font-family: Arial, sans-serif; max-width: 700px; margin: 40px auto; color: #222; }
 h1 { font-size: 20px; } .meta { color: #555; margin-bottom: 24px; }
 table { width: 100%; border-collapse: collapse; font-size: 14px; }
 th, td { border: 1px solid #999; padding: 6px 10px; text-align: left; }
 th { background: #eee; } tfoot td { font-weight: bold; }
</style></head><body>
<h1>SOCIEDAD CONCESIONARIA — DETALLE DE PEAJES</h1>
<div class="meta">
 Patente: <b>$Patente</b><br>
 Período: $((Get-Date).Date.AddDays(-($Dias-1)).ToString("dd-MM-yyyy")) al $((Get-Date).ToString("dd-MM-yyyy"))<br>
 Documento de prueba generado por simular-cruces.ps1
</div>
<table>
<thead><tr><th>Fecha</th><th>Hora</th><th>Pórtico</th><th>Autopista</th><th>Valor</th></tr></thead>
<tbody>
$filas
</tbody>
<tfoot><tr><td colspan="4">TOTAL</td><td style="text-align:right">`$ $("{0:N0}" -f $totalFactura)</td></tr></tfoot>
</table>
</body></html>
"@

$rutaFactura = Join-Path $PSScriptRoot "factura-simulada.html"
[System.IO.File]::WriteAllText($rutaFactura, $html, [System.Text.UTF8Encoding]::new($false))

Write-Host "`nFactura simulada$notaDiferencias generada en:" -ForegroundColor Green
Write-Host "  $rutaFactura"
Write-Host "Ábrela en el navegador e imprímela a PDF (Ctrl+P) o fotografíala desde la app." -ForegroundColor DarkGray
