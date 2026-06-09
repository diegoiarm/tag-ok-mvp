package com.tagok.app.domain.exceptions

sealed class ApplicationError(message: String) : Exception(message)
{
    data object Network :
        ApplicationError("Verifica tu conexión a internet")

    data object Timeout :
        ApplicationError("La operación tardó demasiado")

    data object Unauthorized :
        ApplicationError("Tu sesión ha expirado")

    data object Forbidden :
        ApplicationError("No tienes permisos para realizar esta acción")

    data object NotFound :
        ApplicationError("No se encontraron datos")

    data object Server :
        ApplicationError("El servicio no está disponible")

    data object Unknown :
        ApplicationError("Ocurrió un error inesperado")
}