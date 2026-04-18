package com.tagok.routes_service.domain;

import java.util.ArrayList;
import java.util.List;

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

    private String nombre;

    @Builder.Default
    @OneToMany(mappedBy = "autopista", cascade = CascadeType.ALL)
    private List<Portico> porticos = new ArrayList<>();

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
}