package com.tagok.routes_service.domain.dto.request;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PorticoCobroRequest
{
    private Long id;
    private LocalDateTime fechaCobro;
}
