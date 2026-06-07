package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.tagok.routes_service.domain.calendario.CalendarioTarifario;
import com.tagok.routes_service.domain.calendario.RangoHorario;
import com.tagok.routes_service.domain.calendario.ReglaTemporal;
import com.tagok.routes_service.domain.calendario.TipoDia;

/**
 * Validaciones estructurales de una configuración tarifaria (reglas + calendario)
 * antes de persistirla (CU19). Lanza {@link IllegalArgumentException} con un mensaje
 * legible que el {@code GlobalExceptionHandler} traduce a HTTP 400.
 */
public final class TarifaConfigValidator
{
    private TarifaConfigValidator() {}

    public static void validar(List<ReglaTarifaria> reglas, CalendarioTarifario calendario)
    {
        validarReglasTarifarias(reglas);
        validarCalendario(calendario);
        validarCoberturaDeValores(reglas, calendario);
    }

    private static void validarReglasTarifarias(List<ReglaTarifaria> reglas)
    {
        if (reglas == null || reglas.isEmpty())
            throw new IllegalArgumentException("Debe definir al menos una regla tarifaria.");

        for (ReglaTarifaria regla : reglas)
        {
            if (regla.getAplicaA() == null || regla.getAplicaA().isEmpty())
                throw new IllegalArgumentException("Cada regla tarifaria debe aplicar a al menos un tipo de vehículo.");

            if (regla.getValores() == null || regla.getValores().isEmpty())
                throw new IllegalArgumentException("Cada regla tarifaria debe tener al menos un valor.");

            for (ValorTarifa valor : regla.getValores())
            {
                if (valor.getTipoTarifa() == null)
                    throw new IllegalArgumentException("Cada valor debe indicar su tipo de tarifa.");

                if (valor.getValor() == null || valor.getValor().compareTo(BigDecimal.ZERO) < 0)
                    throw new IllegalArgumentException(
                        "El valor de la tarifa " + valor.getTipoTarifa() + " no puede ser nulo ni negativo.");
            }
        }
    }

    private static void validarCalendario(CalendarioTarifario calendario)
    {
        if (calendario == null || calendario.getReglas() == null || calendario.getReglas().isEmpty())
            throw new IllegalArgumentException("Debe definir al menos una regla de calendario.");

        for (ReglaTemporal regla : calendario.getReglas())
        {
            if (regla.getTipoTarifa() == null)
                throw new IllegalArgumentException("Cada regla de calendario debe indicar el tipo de tarifa que aplica.");

            if (regla.getTipoDia() == null)
                throw new IllegalArgumentException("Cada regla de calendario debe indicar el tipo de día.");

            if (regla.getTramos() == null || regla.getTramos().isEmpty())
                throw new IllegalArgumentException(
                    "La regla de calendario (" + regla.getTipoDia() + ") debe tener al menos un rango horario.");

            for (RangoHorario tramo : regla.getTramos())
            {
                if (tramo.getHoraInicio() == null || tramo.getHoraFin() == null)
                    throw new IllegalArgumentException("Todo rango horario debe tener hora de inicio y de fin.");

                if (!tramo.getHoraInicio().isBefore(tramo.getHoraFin()))
                    throw new IllegalArgumentException(
                        "La hora de inicio (" + tramo.getHoraInicio() + ") debe ser anterior a la de fin ("
                            + tramo.getHoraFin() + ").");
            }
        }

        validarSinSolapes(calendario.getReglas());
    }

    /** Verifica que, dentro de un mismo tipo de día, ningún rango horario se solape con otro. */
    private static void validarSinSolapes(List<ReglaTemporal> reglas)
    {
        for (TipoDia dia : TipoDia.values())
        {
            List<RangoHorario> rangos = new ArrayList<>();
            reglas.stream()
                .filter(r -> r.getTipoDia() == dia)
                .forEach(r -> rangos.addAll(r.getTramos()));

            rangos.sort(Comparator.comparing(RangoHorario::getHoraInicio));

            for (int i = 1; i < rangos.size(); i++)
            {
                RangoHorario previo = rangos.get(i - 1);
                RangoHorario actual = rangos.get(i);

                if (actual.getHoraInicio().isBefore(previo.getHoraFin()))
                    throw new IllegalArgumentException(
                        "Los rangos horarios de " + dia + " se solapan: "
                            + previo.getHoraInicio() + "-" + previo.getHoraFin() + " y "
                            + actual.getHoraInicio() + "-" + actual.getHoraFin() + ".");
            }
        }
    }

    /** Cada {@code TipoTarifa} referenciado por el calendario debe tener valor en todas las reglas. */
    private static void validarCoberturaDeValores(List<ReglaTarifaria> reglas, CalendarioTarifario calendario)
    {
        List<TipoTarifa> requeridos = calendario.getReglas().stream()
            .map(ReglaTemporal::getTipoTarifa)
            .distinct()
            .toList();

        for (ReglaTarifaria regla : reglas)
        {
            for (TipoTarifa requerido : requeridos)
            {
                boolean cubierto = regla.getValores().stream()
                    .anyMatch(v -> v.getTipoTarifa() == requerido);

                if (!cubierto)
                    throw new IllegalArgumentException(
                        "Falta el valor de la tarifa " + requerido + " para los vehículos "
                            + regla.getAplicaA() + ".");
            }
        }
    }
}
