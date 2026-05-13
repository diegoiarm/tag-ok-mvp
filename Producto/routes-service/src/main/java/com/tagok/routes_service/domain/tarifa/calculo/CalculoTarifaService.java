package com.tagok.routes_service.domain.tarifa.calculo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.autopista.TipoCobro;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tarifa.Cruce;
import com.tagok.routes_service.domain.tarifa.CrucePortico;
import com.tagok.routes_service.domain.tarifa.CruceTramo;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;
import com.tagok.routes_service.repository.PorticoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CalculoTarifaService 
{
    private final PorticoRepository porticoRepository;
    private final CalculadorTarifaFactory calculadorFactory;

    public List<Cruce> calcularCruces(List<CruceRequest> cruces, TipoVehiculo vehiculo)
    {
        List<PorticoCruce> porticosCruce = cruces.stream()
            .map(c -> 
            {
                Portico p = porticoRepository.findById(c.porticoId())
                    .orElseThrow(() -> new IllegalArgumentException("Pórtico no encontrado: " + c.porticoId()));

                return new PorticoCruce(p, c.horaFechaCruce());
            })
            .filter(pc -> pc.portico.getAutopista().getTipoCobro() == TipoCobro.TRAMO
                || (pc.portico.getCalendario() != null && !pc.portico.getReglas().isEmpty()))
            .collect(Collectors.toList());

        List<List<PorticoCruce>> gruposContiguos = new ArrayList<>();
        List<PorticoCruce> grupoActual = new ArrayList<>();
        Autopista autopistaActual = null;

        for (PorticoCruce pc : porticosCruce) 
        {
            Autopista autopista = pc.portico.getAutopista();

            if (autopista == null) 
                continue;

            if (grupoActual.isEmpty() || autopista.equals(autopistaActual)) 
            {
                grupoActual.add(pc);
                autopistaActual = autopista;
            } 
            else 
            {
                gruposContiguos.add(grupoActual);
                grupoActual = new ArrayList<>();
                grupoActual.add(pc);
                autopistaActual = autopista;
            }
        }

        if (!grupoActual.isEmpty())
            gruposContiguos.add(grupoActual);

        List<Cruce> resultados = new ArrayList<>();

        CalculoContexto ctx = CalculoContexto.builder()
            .vehiculo(vehiculo)
            .build();

        for (List<PorticoCruce> grupo : gruposContiguos) 
        {
            if (grupo.isEmpty()) 
                continue;

            Autopista autopista = grupo.get(0).portico.getAutopista();
            CalculadorTarifaStrategy strategy = calculadorFactory.getStrategy(autopista);
            ctx.setAutopista(autopista);

            switch (autopista.getTipoCobro()) 
            {
                case PORTICO -> 
                {
                    for (PorticoCruce pc : grupo) 
                    {
                        ctx.setPortico(pc.portico);
                        ctx.setFecha(pc.tiempo);
                        strategy.calcular(ctx).ifPresent(tarifa ->
                            resultados.add(new CrucePortico(
                                pc.portico.getId(),
                                pc.portico.getCodigo(),
                                pc.portico.getNombre(),
                                autopista.getNombre(),
                                pc.portico.getLatitud(),
                                pc.portico.getLongitud(),
                                tarifa.tipoTarifa(),
                                tarifa.monto(),
                                pc.tiempo
                            ))
                        );
                    }
                }
                case TRAMO -> 
                {
                    if (grupo.size() < 2) 
                        continue;

                    PorticoCruce entrada = grupo.get(0);
                    PorticoCruce salida = grupo.get(grupo.size() - 1);

                    if (entrada.portico.getId().equals(salida.portico.getId())) 
                        continue;

                    ctx.setEntrada(entrada.portico);
                    ctx.setSalida(salida.portico);
                    ctx.setPortico(null);
                    ctx.setFecha(salida.tiempo);

                    strategy.calcular(ctx).ifPresent(tarifa ->
                        resultados.add(new CruceTramo(
                            entrada.portico.getCodigo() + "-" + salida.portico.getCodigo(),
                            entrada.portico.getNombre() + " -> " + salida.portico.getNombre(),
                            autopista.getNombre(),
                            tarifa.tipoTarifa(),
                            tarifa.monto(),
                            salida.tiempo,
                            entrada.portico.getId(),
                            salida.portico.getId(),
                            entrada.portico.getNombre(),
                            salida.portico.getNombre(),
                            entrada.portico.getLatitud(),
                            entrada.portico.getLongitud(),
                            salida.portico.getLatitud(),
                            salida.portico.getLongitud()
                        ))
                    );
                }
            }
        }

        return resultados;
    }

    private record PorticoCruce(Portico portico, LocalDateTime tiempo) {}
}
