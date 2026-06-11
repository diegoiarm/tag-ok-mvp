package com.tagok.history_service.ia;

public class IAException extends RuntimeException
{
    public IAException(String message)
    {
        super(message);
    }

    public IAException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
