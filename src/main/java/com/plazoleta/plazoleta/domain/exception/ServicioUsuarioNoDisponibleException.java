package com.plazoleta.plazoleta.domain.exception;

public class ServicioUsuarioNoDisponibleException extends DominioException {

    public ServicioUsuarioNoDisponibleException(String message) {
        super(message);
    }

    public ServicioUsuarioNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}