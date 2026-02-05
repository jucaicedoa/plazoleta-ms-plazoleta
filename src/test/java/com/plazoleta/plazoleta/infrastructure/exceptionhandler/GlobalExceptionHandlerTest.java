package com.plazoleta.plazoleta.infrastructure.exceptionhandler;

import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.exception.PlatoNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.RestauranteNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.RestauranteNoPerteneceException;
import com.plazoleta.plazoleta.domain.exception.RolNoAutorizadoException;
import com.plazoleta.plazoleta.domain.exception.ServicioUsuarioNoDisponibleException;
import com.plazoleta.plazoleta.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.plazoleta.infraestructure.exceptionhandler.GlobalExceptionHandler;
import com.plazoleta.plazoleta.infraestructure.exceptionhandler.dto.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests - GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Debe retornar 404 para UsuarioNoEncontradoException")
    void shouldHandleUsuarioNoEncontrado() {
        ResponseEntity<ErrorResponseDto> response = handler.handleUsuarioNoEncontrado(
                new UsuarioNoEncontradoException("Usuario no existe"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Usuario no existe");
    }

    @Test
    @DisplayName("Debe retornar 404 para PlatoNoEncontradoException")
    void shouldHandlePlatoNoEncontrado() {
        ResponseEntity<ErrorResponseDto> response = handler.handlePlatoNoEncontrado(
                new PlatoNoEncontradoException("Plato no existe"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Plato no existe");
    }

    @Test
    @DisplayName("Debe retornar 404 para RestauranteNoEncontradoException")
    void shouldHandleRestauranteNoEncontrado() {
        ResponseEntity<ErrorResponseDto> response = handler.handleRestauranteNoEncontrado(
                new RestauranteNoEncontradoException("Restaurante no existe"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Restaurante no existe");
    }

    @Test
    @DisplayName("Debe retornar 403 para RolNoAutorizadoException")
    void shouldHandleRolNoAutorizado() {
        ResponseEntity<ErrorResponseDto> response = handler.handleRolNoAutorizado(
                new RolNoAutorizadoException("Rol no autorizado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getStatus()).isEqualTo(403);
        assertThat(response.getBody().getError()).isEqualTo("Forbidden");
    }

    @Test
    @DisplayName("Debe retornar 403 para RestauranteNoPerteneceException")
    void shouldHandleRestauranteNoPertenece() {
        ResponseEntity<ErrorResponseDto> response = handler.handleRestauranteNoPertenece(
                new RestauranteNoPerteneceException("Restaurante no pertenece al propietario"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getMessage()).isEqualTo("Restaurante no pertenece al propietario");
    }

    @Test
    @DisplayName("Debe retornar 502 para ServicioUsuarioNoDisponibleException")
    void shouldHandleServicioUsuarioNoDisponible() {
        ResponseEntity<ErrorResponseDto> response = handler.handleServicioUsuarioNoDisponible(
                new ServicioUsuarioNoDisponibleException("Servicio no disponible"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody().getStatus()).isEqualTo(502);
        assertThat(response.getBody().getError()).isEqualTo("Bad Gateway");
    }

    @Test
    @DisplayName("Debe retornar 400 para DominioException")
    void shouldHandleDominioException() {
        ResponseEntity<ErrorResponseDto> response = handler.handleDominioException(
                new DominioException("Error de validación"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Error de validación");
    }

    @Test
    @DisplayName("Debe retornar 500 para excepción genérica")
    void shouldHandleGenericException() {
        ResponseEntity<ErrorResponseDto> response = handler.handleGeneric(
                new RuntimeException("Error inesperado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Error interno del servidor");
    }

    @Test
    @DisplayName("Debe incluir timestamp en la respuesta de error")
    void shouldIncludeTimestampInErrorResponse() {
        ResponseEntity<ErrorResponseDto> response = handler.handleUsuarioNoEncontrado(
                new UsuarioNoEncontradoException("Test"));

        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}