package com.tagok.history_service.domain;

import lombok.Data;
import java.util.List;

@Data
public class Dia {
    private String fechaDia;
    private List<PorticoCruce> porticos;
}
