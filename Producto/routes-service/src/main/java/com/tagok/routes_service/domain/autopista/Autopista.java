package com.tagok.routes_service.domain.autopista;

import java.util.ArrayList;
import java.util.List;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tramo.Tramo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Autopista 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private TipoCobro tipoCobro;

    private String nombre;
    private String codigo;

    @Builder.Default
    @OneToMany(mappedBy = "autopista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Portico> porticos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "autopista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tramo> tramos = new ArrayList<>();

    public void addPortico(Portico portico)
    {
        boolean existe = porticos.stream()
            .anyMatch(p -> p.getCodigo().equals(portico.getCodigo()));

        if (!existe) 
        {
            porticos.add(portico);
            portico.setAutopista(this);
        }
    }

    public void addTramo(Tramo tramo)
    {
        boolean existe = tramos.stream()
            .anyMatch(t -> t.getEntrada().getCodigo().equals(tramo.getEntrada().getCodigo()) && t.getSalida().getCodigo().equals(tramo.getSalida().getCodigo()));

        if (!existe) 
        {
            tramos.add(tramo);
            tramo.setAutopista(this);
        }
    }
}