package com.tagok.routes_service.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CalendarioTarifario 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne
    @JoinColumn(name = "portico_id")
    private Portico portico;

    @OneToMany(mappedBy = "calendario", cascade = CascadeType.ALL)
    private List<ReglaTemporal> reglas = new ArrayList<>();

    public void addRegla(ReglaTemporal regla) 
    {
        reglas.add(regla);
        regla.setCalendario(this);
    }
}
