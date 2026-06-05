package com.tagok.history_service.controller.dto;

import java.util.List;

import lombok.Data;

@Data
public class FiltroHistorialRequest 
{
    private List<String> patentes;
    private List<String> autopistas;
}
