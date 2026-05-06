package com.tagok.routes_service.domain.tramo;

import java.util.ArrayList;
import java.util.List;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.calendario.CalendarioTarifario;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tarifa.ReglaTarifaria;

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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tramo 
{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @ManyToOne
    private Portico entrada;

    @ManyToOne
    private Portico salida;

    private double distanciaKm;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tramo_id")
    private List<ReglaTarifaria> reglas = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "calendario_id")
    private CalendarioTarifario calendario;

    @ManyToOne
    @JoinColumn(name = "autopista_id")
    private Autopista autopista;

    public void addRegla(ReglaTarifaria regla)
    {
        reglas.add(regla);
    }

    public void setCalendario(CalendarioTarifario calendario)
    {
        this.calendario = calendario;
    }
}
