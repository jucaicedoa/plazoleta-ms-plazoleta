package com.plazoleta.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta.application.dto.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.application.handler.IRestauranteHandler;
import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.exception.RolNoAutorizadoException;
import com.plazoleta.plazoleta.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.plazoleta.infraestructure.input.rest.RestauranteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests de Integración - RestauranteController")
class RestauranteControllerTest {

    private static final String BASE_URL = "/api/v1/restaurantes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IRestauranteHandler restauranteHandler;

    @Test
    @DisplayName("POST /api/v1/restaurantes - Debe retornar 201 CREATED con datos válidos")
    void shouldReturn201WhenCreatingRestaurantSuccessfully() throws Exception {
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doNothing().when(restauranteHandler).createRestaurante(any(CrearRestauranteRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(restauranteHandler, times(1)).createRestaurante(any(CrearRestauranteRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/restaurantes - Debe retornar 400 cuando el NIT no es numérico")
    void shouldReturn400WhenNitIsNotNumeric() throws Exception {
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900-123-456");

        doThrow(new DominioException("El NIT debe ser numérico"))
                .when(restauranteHandler).createRestaurante(any(CrearRestauranteRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El NIT debe ser numérico"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /api/v1/restaurantes - Debe retornar 404 cuando el usuario no existe")
    void shouldReturn404WhenUserDoesNotExist() throws Exception {
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(999L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doThrow(new UsuarioNoEncontradoException("El usuario propietario no existe"))
                .when(restauranteHandler).createRestaurante(any(CrearRestauranteRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario propietario no existe"));
    }

    @Test
    @DisplayName("POST /api/v1/restaurantes - Debe retornar 403 cuando el usuario no es propietario")
    void shouldReturn403WhenUserIsNotOwner() throws Exception {
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doThrow(new RolNoAutorizadoException("El usuario no tiene rol de propietario"))
                .when(restauranteHandler).createRestaurante(any(CrearRestauranteRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("El usuario no tiene rol de propietario"));
    }

    @Test
    @DisplayName("POST /api/v1/restaurantes - Debe retornar 500 cuando ocurre error inesperado")
    void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("La Arepa Feliz");
        dto.setDireccion("Calle 123 #45-67");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://ejemplo.com/logo.png");
        dto.setNit("900123456");

        doThrow(new RuntimeException("Error inesperado"))
                .when(restauranteHandler).createRestaurante(any(CrearRestauranteRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    @DisplayName("POST /api/v1/restaurantes - Debe incluir timestamp en respuestas de error")
    void shouldIncludeTimestampInErrorResponses() throws Exception {
        CrearRestauranteRequestDto dto = new CrearRestauranteRequestDto();
        dto.setNombre("12345");
        dto.setDireccion("Calle 123");
        dto.setPropietarioId(1L);
        dto.setTelefono("+573001234567");
        dto.setUrlLogo("https://test.com");
        dto.setNit("123456789");

        doThrow(new DominioException("Error de validación"))
                .when(restauranteHandler).createRestaurante(any(CrearRestauranteRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}