package com.tagok.routes_service.domain;

import java.util.ArrayList;
import java.util.List;

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
    private List<ReglaTarifaria> reglas = new ArrayList<>();

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

    public void setCalendario(CalendarioTarifario calendario) 
    {
        this.calendario = calendario;
        
        if (calendario != null)
            calendario.setPortico(this);
    }

    public double calcularValor()
    {
        return Double.MAX_VALUE;
    }
}
