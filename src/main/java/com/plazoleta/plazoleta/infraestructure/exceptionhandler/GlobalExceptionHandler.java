package com.plazoleta.plazoleta.infraestructure.exceptionhandler;

import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.exception.PlatoNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.RestauranteNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.RestauranteNoPerteneceException;
import com.plazoleta.plazoleta.domain.exception.RolNoAutorizadoException;
import com.plazoleta.plazoleta.domain.exception.ServicioUsuarioNoDisponibleException;
import com.plazoleta.plazoleta.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.plazoleta.infraestructure.exceptionhandler.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String NOT_FOUND_TITLE = "Not Found";

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return buildError(HttpStatus.NOT_FOUND, NOT_FOUND_TITLE, ex.getMessage());
    }

    @ExceptionHandler(PlatoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> handlePlatoNoEncontrado(PlatoNoEncontradoException ex) {
        return buildError(HttpStatus.NOT_FOUND, NOT_FOUND_TITLE, ex.getMessage());
    }

    @ExceptionHandler(RestauranteNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> handleRestauranteNoEncontrado(RestauranteNoEncontradoException ex) {
        return buildError(HttpStatus.NOT_FOUND, NOT_FOUND_TITLE, ex.getMessage());
    }

    @ExceptionHandler(RolNoAutorizadoException.class)
    public ResponseEntity<ErrorResponseDto> handleRolNoAutorizado(RolNoAutorizadoException ex) {
        return buildError(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    @ExceptionHandler(RestauranteNoPerteneceException.class)
    public ResponseEntity<ErrorResponseDto> handleRestauranteNoPertenece(RestauranteNoPerteneceException ex) {
        return buildError(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    @ExceptionHandler(ServicioUsuarioNoDisponibleException.class)
    public ResponseEntity<ErrorResponseDto> handleServicioUsuarioNoDisponible(ServicioUsuarioNoDisponibleException ex) {
        return buildError(HttpStatus.BAD_GATEWAY, "Bad Gateway", ex.getMessage());
    }

    @ExceptionHandler(DominioException.class)
    public ResponseEntity<ErrorResponseDto> handleDominioException(DominioException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Error interno del servidor");
    }

    private static ResponseEntity<ErrorResponseDto> buildError(HttpStatus status, String error, String message) {
        ErrorResponseDto body = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}