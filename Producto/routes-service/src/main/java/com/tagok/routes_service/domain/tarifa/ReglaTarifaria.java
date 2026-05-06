package com.tagok.routes_service.domain.tarifa;

import java.util.ArrayList;
import java.util.List;

import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class ReglaTarifaria 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ElementCollection
    @CollectionTable(name = "regla_tarifaria_vehiculos", joinColumns = @JoinColumn(name = "regla_id"))
    @Column(name = "tipo_vehiculo")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<TipoVehiculo> aplicaA = new ArrayList<>();

    @OneToMany(mappedBy = "regla", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ValorTarifa> valores = new ArrayList<>();

    public void addValor(ValorTarifa valor) 
    {
        valores.add(valor);
        valor.setRegla(this);
    }

    public boolean aplicaATipo(TipoVehiculo tipo)
    {
        return aplicaA.contains(tipo);
    }

    public ValorTarifa obtenerValor(TipoTarifa tipoTarifa)
    {
        return valores.stream()
            .filter(v -> v.getTipoTarifa() == tipoTarifa)
            .findFirst()
            .orElseGet(() -> {
                log.warn("Falta tarifa {}, usando TBFP", tipoTarifa);

                return valores.stream()
                    .filter(v -> v.getTipoTarifa() == TipoTarifa.TBFP)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                        "No hay TBFP definida"
                    ));
            });
    }
}