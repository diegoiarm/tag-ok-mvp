package com.tagok.history_service.service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.tagok.history_service.dto.BoletaDTO;
import com.tagok.history_service.dto.BoletaItemDTO;
import com.tagok.history_service.dto.ComparacionFacturaDTO;
import com.tagok.history_service.dto.ComparacionItemDTO;
import com.tagok.history_service.dto.EstadoComparacion;
import com.tagok.history_service.dto.FacturaExtraidaDTO;
import com.tagok.history_service.dto.FacturaItemDTO;

/**
 * Comparación determinista entre la boleta generada por la app y la factura
 * extraída por la IA. El emparejamiento es greedy en tres pasadas:
 * 1) misma fecha + mismo valor + nombre similar  → COINCIDE
 * 2) misma fecha + nombre similar                → MONTO_DIFERENTE
 * 3) misma fecha + mismo valor                   → COINCIDE (nombres escritos distinto)
 * Lo que quede sin pareja es SOLO_EN_APP / SOLO_EN_FACTURA.
 */
@Component
public class ComparadorFacturas
{
    public ComparacionFacturaDTO comparar(BoletaDTO boletaApp, FacturaExtraidaDTO factura)
    {
        List<BoletaItemDTO> pendientesApp = new ArrayList<>(
            boletaApp.getItems() != null ? boletaApp.getItems() : List.of());
        List<FacturaItemDTO> pendientesFactura = new ArrayList<>(
            factura.getItems() != null ? factura.getItems() : List.of());

        List<ComparacionItemDTO> resultado = new ArrayList<>();

        emparejar(pendientesApp, pendientesFactura, resultado, true, true);
        emparejar(pendientesApp, pendientesFactura, resultado, false, true);
        emparejar(pendientesApp, pendientesFactura, resultado, true, false);

        for (BoletaItemDTO app : pendientesApp)
        {
            resultado.add(ComparacionItemDTO.builder()
                .estado(EstadoComparacion.SOLO_EN_APP)
                .itemApp(app)
                .diferenciaValor(negar(app.getValor()))
                .build());
        }
        for (FacturaItemDTO cliente : pendientesFactura)
        {
            resultado.add(ComparacionItemDTO.builder()
                .estado(EstadoComparacion.SOLO_EN_FACTURA)
                .itemFactura(cliente)
                .diferenciaValor(cliente.getValor())
                .build());
        }

        BigDecimal totalApp = boletaApp.getTotal() != null ? boletaApp.getTotal() : BigDecimal.ZERO;
        BigDecimal totalFactura = factura.getTotal() != null
            ? factura.getTotal()
            : sumarItems(factura.getItems());
        BigDecimal diferenciaTotal = totalFactura.subtract(totalApp);

        long coincidencias = resultado.stream()
            .filter(i -> i.getEstado() == EstadoComparacion.COINCIDE).count();
        long discrepancias = resultado.size() - coincidencias;

        return ComparacionFacturaDTO.builder()
            .boletaApp(boletaApp)
            .facturaCliente(factura)
            .items(resultado)
            .totalApp(totalApp)
            .totalFactura(totalFactura)
            .diferenciaTotal(diferenciaTotal)
            .coincidencias(coincidencias)
            .discrepancias(discrepancias)
            .cuadra(discrepancias == 0 && diferenciaTotal.compareTo(BigDecimal.ZERO) == 0)
            .build();
    }

    private void emparejar(List<BoletaItemDTO> pendientesApp, List<FacturaItemDTO> pendientesFactura,
                           List<ComparacionItemDTO> resultado, boolean exigirValor, boolean exigirNombre)
    {
        for (var itApp = pendientesApp.iterator(); itApp.hasNext();)
        {
            BoletaItemDTO app = itApp.next();

            for (var itFac = pendientesFactura.iterator(); itFac.hasNext();)
            {
                FacturaItemDTO cliente = itFac.next();

                if (!mismaFecha(app.getFecha(), cliente.getFecha()))
                    continue;
                if (exigirValor && !mismoValor(app.getValor(), cliente.getValor()))
                    continue;
                if (exigirNombre && !nombresSimilares(app, cliente))
                    continue;

                boolean valoresIguales = mismoValor(app.getValor(), cliente.getValor());
                resultado.add(ComparacionItemDTO.builder()
                    .estado(valoresIguales ? EstadoComparacion.COINCIDE : EstadoComparacion.MONTO_DIFERENTE)
                    .itemApp(app)
                    .itemFactura(cliente)
                    .diferenciaValor(diferencia(app.getValor(), cliente.getValor()))
                    .build());

                itFac.remove();
                itApp.remove();
                break;
            }
        }
    }

    private boolean mismaFecha(LocalDate fechaApp, String fechaFactura)
    {
        LocalDate fechaCliente = parsearFecha(fechaFactura);
        // Si la IA no pudo leer la fecha, no la usamos como criterio excluyente
        if (fechaCliente == null)
            return true;
        return fechaCliente.equals(fechaApp);
    }

    private LocalDate parsearFecha(String fecha)
    {
        if (fecha == null || fecha.isBlank())
            return null;
        try
        {
            return LocalDate.parse(fecha);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private boolean mismoValor(BigDecimal a, BigDecimal b)
    {
        if (a == null || b == null)
            return false;
        return a.compareTo(b) == 0;
    }

    private BigDecimal diferencia(BigDecimal valorApp, BigDecimal valorFactura)
    {
        BigDecimal app = valorApp != null ? valorApp : BigDecimal.ZERO;
        BigDecimal factura = valorFactura != null ? valorFactura : BigDecimal.ZERO;
        return factura.subtract(app);
    }

    private BigDecimal negar(BigDecimal valor)
    {
        return valor != null ? valor.negate() : BigDecimal.ZERO;
    }

    private boolean nombresSimilares(BoletaItemDTO app, FacturaItemDTO cliente)
    {
        String nombreApp = normalizar(app.getNombre());
        String nombreCliente = normalizar(cliente.getPortico());

        if (nombreApp.isBlank() || nombreCliente.isBlank())
            return false;
        if (nombreApp.contains(nombreCliente) || nombreCliente.contains(nombreApp))
            return true;

        Set<String> tokensApp = new HashSet<>(Arrays.asList(nombreApp.split(" ")));
        Set<String> tokensCliente = new HashSet<>(Arrays.asList(nombreCliente.split(" ")));
        Set<String> interseccion = new HashSet<>(tokensApp);
        interseccion.retainAll(tokensCliente);

        int menor = Math.min(tokensApp.size(), tokensCliente.size());
        return menor > 0 && interseccion.size() * 2 >= menor;
    }

    private String normalizar(String texto)
    {
        if (texto == null)
            return "";
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        return sinAcentos.toUpperCase()
            .replaceAll("[^A-Z0-9 ]", " ")
            .replaceAll("\\s+", " ")
            .trim();
    }

    private BigDecimal sumarItems(List<FacturaItemDTO> items)
    {
        if (items == null)
            return BigDecimal.ZERO;
        return items.stream()
            .map(FacturaItemDTO::getValor)
            .filter(v -> v != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
