package com.tagok.routes_service.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class ReglaTemporal 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "calendario_id")
    private CalendarioTarifario calendario;

    @Enumerated(EnumType.STRING)
    private TipoTarifa tipoTarifa;

    @OneToMany(mappedBy = "regla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RangoHorario> tramos = new ArrayList<>();

    public void addTramo(RangoHorario tramo)
    {
        tramos.add(tramo);
        tramo.setRegla(this);
    }
}
