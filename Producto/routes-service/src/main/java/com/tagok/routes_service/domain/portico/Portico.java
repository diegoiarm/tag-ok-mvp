package com.tagok.routes_service.domain.portico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.calendario.CalendarioTarifario;
import com.tagok.routes_service.domain.tarifa.ReglaTarifaria;
import com.tagok.routes_service.domain.tarifa.Tarifa;
import com.tagok.routes_service.domain.tarifa.TipoTarifa;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Portico 
{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String codigo;
    private String nombre;
    private String sentido;

    private double latitud;
    private double longitud;

    @Builder.Default
    @OneToMany(mappedBy = "portico", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ReglaTarifaria> reglas = new ArrayList<>();

    @OneToOne(mappedBy = "portico", cascade = CascadeType.ALL, orphanRemoval = true)
    private CalendarioTarifario calendario;

    @ManyToOne
    @JoinColumn(name = "autopista_id")
    private Autopista autopista;

    public void addRegla(ReglaTarifaria regla) 
    {
        reglas.add(regla);
        regla.setPortico(this);
    }

    public void removeRegla(ReglaTarifaria regla)
    {
        reglas.remove(regla);
        regla.setPortico(null);
    }

    public void setCalendario(CalendarioTarifario calendario) 
    {
        this.calendario = calendario;
        
        if (calendario != null)
            calendario.setPortico(this);
    }

    public Optional<Tarifa> calcularTarifa(TipoVehiculo vehiculo, LocalDateTime fecha)
    {
        // 1. Validaciones base
        if (this.calendario == null || this.reglas == null || this.reglas.isEmpty()) 
            return Optional.empty();

        // 2. Buscar regla sin excepción
        Optional<ReglaTarifaria> reglaOpt = reglas.stream()
            .filter(r -> r.aplicaATipo(vehiculo))
            .findFirst();

        if (reglaOpt.isEmpty())
            return Optional.empty();

        ReglaTarifaria regla = reglaOpt.get();

        // 3. Calcular tarifa
        TipoTarifa tipoTarifa = this.calendario.obtenerTipoTarifa(fecha);

        BigDecimal monto = regla.obtenerValor(tipoTarifa)
            .getValor();

        return Optional.of(new Tarifa(monto, tipoTarifa));
    }
}