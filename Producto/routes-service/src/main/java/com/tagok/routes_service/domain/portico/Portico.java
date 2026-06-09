package com.tagok.routes_service.domain.portico;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.calendario.CalendarioTarifario;
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

    /** Estado vigente del pórtico: true = vigente, false = desactivado (histórico). */
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "portico_id")
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