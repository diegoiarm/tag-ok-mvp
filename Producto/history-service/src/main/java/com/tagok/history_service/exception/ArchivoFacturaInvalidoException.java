package com.tagok.history_service.exception;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class ArchivoFacturaInvalidoException extends AppException 
{

    public ArchivoFacturaInvalidoException(String message) 
    {
        super("HIST-003", "Archivo de factura inválido: " + message, StandardErrorCategories.INVALID_INPUT, "Archivo de factura inválido: " + message);
    }
}
