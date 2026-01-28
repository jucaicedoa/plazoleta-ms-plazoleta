package com.plazoleta.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta.application.dto.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.application.handler.CrearRestauranteHandler;
import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.infraestructure.input.rest.RestauranteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestauranteController.class)
@DisplayName("Tests de Integración - RestaurantController")
class RestauranteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CrearRestauranteHandler handler;

    @Test
    @DisplayName("POST /restaurantes - Debe retornar 201 CREATED con datos válidos")
    void shouldReturn201WhenCreatingRestaurantSuccessfully() throws Exception {
        // Arrange
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doNothing().when(handler).handle(any(CrearRestauranteRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(handler, times(1)).handle(any(CrearRestauranteRequestDto.class));
    }

    @Test
    @DisplayName("POST /restaurantes - Debe retornar 400 cuando el NIT no es numérico")
    void shouldReturn400WhenNitIsNotNumeric() throws Exception {
        // Arrange
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900-123-456");

        doThrow(new DominioException("El NIT debe ser numérico"))
                .when(handler).handle(any(CrearRestauranteRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El NIT debe ser numérico"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /restaurantes - Debe retornar 400 cuando el teléfono excede 13 caracteres")
    void shouldReturn400WhenPhoneExceeds13Characters() throws Exception {
        // Arrange
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+57300123456789");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doThrow(new DominioException("El teléfono debe tener máximo 13 caracteres"))
                .when(handler).handle(any(CrearRestauranteRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El teléfono debe tener máximo 13 caracteres"));
    }

    @Test
    @DisplayName("POST /restaurantes - Debe retornar 400 cuando el usuario no existe")
    void shouldReturn400WhenUserDoesNotExist() throws Exception {
        // Arrange
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(999L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doThrow(new DominioException("El usuario propietario no existe"))
                .when(handler).handle(any(CrearRestauranteRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El usuario propietario no existe"));
    }

    @Test
    @DisplayName("POST /restaurantes - Debe retornar 400 cuando el usuario no es propietario")
    void shouldReturn400WhenUserIsNotOwner() throws Exception {
        // Arrange
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doThrow(new DominioException("El usuario no tiene rol de propietario"))
                .when(handler).handle(any(CrearRestauranteRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El usuario no tiene rol de propietario"));
    }

    @Test
    @DisplayName("POST /restaurantes - Debe retornar 400 cuando el nombre es solo números")
    void shouldReturn400WhenNameIsOnlyNumbers() throws Exception {
        // Arrange
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("12345");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doThrow(new DominioException("El nombre del restaurante no puede contener solo números"))
                .when(handler).handle(any(CrearRestauranteRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El nombre del restaurante no puede contener solo números"));
    }

    @Test
    @DisplayName("POST /restaurantes - Debe retornar 500 cuando ocurre error inesperado")
    void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {
        // Arrange
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doThrow(new RuntimeException("Error inesperado"))
                .when(handler).handle(any(CrearRestauranteRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    @DisplayName("POST /restaurantes - Debe incluir timestamp en respuestas de error")
    void shouldIncludeTimestampInErrorResponses() throws Exception {
        // Arrange
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("12345");
        dto.setDireccion("Calle 123");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://test.com");
        dto.setNit("123456789");

        doThrow(new DominioException("Error de validación"))
                .when(handler).handle(any(CrearRestauranteRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}