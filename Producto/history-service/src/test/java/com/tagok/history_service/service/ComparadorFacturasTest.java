package com.tagok.history_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.tagok.history_service.dto.BoletaDTO;
import com.tagok.history_service.dto.BoletaItemDTO;
import com.tagok.history_service.dto.ComparacionFacturaDTO;
import com.tagok.history_service.dto.EstadoComparacion;
import com.tagok.history_service.dto.FacturaExtraidaDTO;
import com.tagok.history_service.dto.FacturaItemDTO;

class ComparadorFacturasTest
{
    private final ComparadorFacturas comparador = new ComparadorFacturas();

    private BoletaItemDTO itemApp(String fecha, String nombre, String valor)
    {
        return BoletaItemDTO.builder()
            .fecha(LocalDate.parse(fecha))
            .autopista("Costanera Norte")
            .nombre(nombre)
            .tipoTarifa("TBFP")
            .valor(new BigDecimal(valor))
            .horaCruce("08:15:00")
            .build();
    }

    private FacturaItemDTO itemFactura(String fecha, String portico, String valor)
    {
        return FacturaItemDTO.builder()
            .fecha(fecha)
            .portico(portico)
            .valor(new BigDecimal(valor))
            .build();
    }

    private BoletaDTO boleta(List<BoletaItemDTO> items, String total)
    {
        return BoletaDTO.builder()
            .patente("ABCD12")
            .fechaDesde(LocalDate.parse("2026-05-01"))
            .fechaHasta(LocalDate.parse("2026-05-31"))
            .items(items)
            .total(new BigDecimal(total))
            .build();
    }

    @Test
    void boletaYFacturaIdenticasCuadran()
    {
        BoletaDTO app = boleta(List.of(itemApp("2026-05-10", "Pórtico Vivaceta", "850")), "850");
        FacturaExtraidaDTO factura = FacturaExtraidaDTO.builder()
            .total(new BigDecimal("850"))
            .items(List.of(itemFactura("2026-05-10", "PORTICO VIVACETA", "850")))
            .build();

        ComparacionFacturaDTO resultado = comparador.comparar(app, factura);

        assertTrue(resultado.isCuadra());
        assertEquals(1, resultado.getCoincidencias());
        assertEquals(0, resultado.getDiscrepancias());
        assertEquals(EstadoComparacion.COINCIDE, resultado.getItems().get(0).getEstado());
    }

    @Test
    void detectaMontoDiferenteEnMismoPortico()
    {
        BoletaDTO app = boleta(List.of(itemApp("2026-05-10", "Pórtico Vivaceta", "850")), "850");
        FacturaExtraidaDTO factura = FacturaExtraidaDTO.builder()
            .total(new BigDecimal("1200"))
            .items(List.of(itemFactura("2026-05-10", "Portico Vivaceta", "1200")))
            .build();

        ComparacionFacturaDTO resultado = comparador.comparar(app, factura);

        assertFalse(resultado.isCuadra());
        assertEquals(EstadoComparacion.MONTO_DIFERENTE, resultado.getItems().get(0).getEstado());
        assertEquals(0, new BigDecimal("350").compareTo(resultado.getItems().get(0).getDiferenciaValor()));
        assertEquals(0, new BigDecimal("350").compareTo(resultado.getDiferenciaTotal()));
    }

    @Test
    void detectaCrucesSoloEnUnLado()
    {
        BoletaDTO app = boleta(List.of(
            itemApp("2026-05-10", "Pórtico Vivaceta", "850"),
            itemApp("2026-05-11", "Pórtico Lo Saldes", "600")), "1450");
        FacturaExtraidaDTO factura = FacturaExtraidaDTO.builder()
            .items(List.of(
                itemFactura("2026-05-10", "PORTICO VIVACETA", "850"),
                itemFactura("2026-05-12", "PORTICO TUNEL SAN CRISTOBAL", "900")))
            .build();

        ComparacionFacturaDTO resultado = comparador.comparar(app, factura);

        assertEquals(1, resultado.getCoincidencias());
        assertEquals(2, resultado.getDiscrepancias());
        assertTrue(resultado.getItems().stream()
            .anyMatch(i -> i.getEstado() == EstadoComparacion.SOLO_EN_APP));
        assertTrue(resultado.getItems().stream()
            .anyMatch(i -> i.getEstado() == EstadoComparacion.SOLO_EN_FACTURA));
        // total factura sin campo total: se suma desde los items (850 + 900)
        assertEquals(0, new BigDecimal("1750").compareTo(resultado.getTotalFactura()));
    }

    @Test
    void empatamientoPorValorCuandoNombresDifieren()
    {
        BoletaDTO app = boleta(List.of(itemApp("2026-05-10", "Pórtico P14 Centro Oriente", "780")), "780");
        FacturaExtraidaDTO factura = FacturaExtraidaDTO.builder()
            .total(new BigDecimal("780"))
            .items(List.of(itemFactura("2026-05-10", "Sector Las Condes", "780")))
            .build();

        ComparacionFacturaDTO resultado = comparador.comparar(app, factura);

        assertTrue(resultado.isCuadra());
        assertEquals(EstadoComparacion.COINCIDE, resultado.getItems().get(0).getEstado());
    }
}
