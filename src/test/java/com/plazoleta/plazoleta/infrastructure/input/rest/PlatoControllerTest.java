package com.plazoleta.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta.application.dto.CrearPlatoRequestDto;
import com.plazoleta.plazoleta.application.dto.ActualizarPlatoRequestDto;
import com.plazoleta.plazoleta.application.handler.CrearPlatoHandler;
import com.plazoleta.plazoleta.application.handler.ActualizarPlatoHandler;
import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.infraestructure.input.rest.PlatoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlatoController.class)
class PlatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CrearPlatoHandler crearPlatoHandler;
    @MockitoBean
    private ActualizarPlatoHandler actualizarPlatoHandler;
    private CrearPlatoRequestDto validDto;
    private String validJson;
    private ActualizarPlatoRequestDto actualizarDto;
    private String updateJson;

    @BeforeEach
    void setUp() throws Exception {
        validDto = new CrearPlatoRequestDto();
        validDto.setNombre("Hamburguesa Especial");
        validDto.setPrecio(25000);
        validDto.setDescripcion("Carne artesanal con queso cheddar");
        validDto.setUrlImagen("http://img.com/burger.png");
        validDto.setCategoria("COMIDA_RAPIDA");
        validDto.setRestauranteId(10L);

        validJson = objectMapper.writeValueAsString(validDto);

        actualizarDto = new ActualizarPlatoRequestDto();
        actualizarDto.setPrecio(18000);
        actualizarDto.setDescripcion("Hamburguesa artesanal con queso y tocineta");
        updateJson = objectMapper.writeValueAsString(actualizarDto);
    }

    @Test
    @DisplayName("POST /platos - Debería crear plato exitosamente y retornar 201")
    void shouldCreateDishSuccessfully() throws Exception {
        // Given
        Long propietarioId = 1L;
        doNothing().when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), eq(propietarioId));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", propietarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated());

        verify(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), eq(propietarioId));
    }

    @Test
    @DisplayName("POST /platos - Debería retornar 400 cuando faltan datos obligatorios")
    void shouldReturn400WhenMissingRequiredFields() throws Exception {
        // Given
        CrearPlatoRequestDto invalidDto = new CrearPlatoRequestDto();
        invalidDto.setNombre("");
        invalidDto.setPrecio(25000);
        invalidDto.setRestauranteId(10L);

        String invalidJson = objectMapper.writeValueAsString(invalidDto);

        doThrow(new DominioException("El nombre del plato es obligatorio"))
                .when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), any(Long.class));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /platos - Debería retornar 400 cuando el precio es cero")
    void shouldReturn400WhenPriceIsZero() throws Exception {
        // Given
        validDto.setPrecio(0);
        String jsonWithZeroPrice = objectMapper.writeValueAsString(validDto);

        doThrow(new DominioException("El precio debe ser mayor a cero"))
                .when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), any(Long.class));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithZeroPrice))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /platos - Debería retornar 400 cuando el precio es negativo")
    void shouldReturn400WhenPriceIsNegative() throws Exception {
        // Given
        validDto.setPrecio(-5000);
        String jsonWithNegativePrice = objectMapper.writeValueAsString(validDto);

        doThrow(new DominioException("El precio debe ser mayor a cero"))
                .when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), any(Long.class));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNegativePrice))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /platos - Debería retornar 400 cuando el usuario no es propietario")
    void shouldReturn400WhenUserIsNotOwner() throws Exception {
        // Given
        doThrow(new DominioException("El usuario no tiene el rol de propietario"))
                .when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), any(Long.class));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /platos - Debería retornar 400 cuando el restaurante no existe")
    void shouldReturn400WhenRestaurantDoesNotExist() throws Exception {
        // Given
        validDto.setRestauranteId(999L);
        String jsonWithInvalidRestaurant = objectMapper.writeValueAsString(validDto);

        doThrow(new DominioException("El restaurante especificado no existe"))
                .when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), any(Long.class));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithInvalidRestaurant))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /platos - Debería retornar 400 cuando el restaurante no pertenece al propietario")
    void shouldReturn400WhenRestaurantDoesNotBelongToOwner() throws Exception {
        // Given
        doThrow(new DominioException("El restaurante no pertenece al propietario"))
                .when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), any(Long.class));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /platos - Debería aceptar diferentes categorías")
    void shouldAcceptDifferentCategories() throws Exception {
        // Given
        validDto.setCategoria("PIZZA");
        String jsonWithPizza = objectMapper.writeValueAsString(validDto);

        doNothing().when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), any(Long.class));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithPizza))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /platos - Debería aceptar precios altos")
    void shouldAcceptHighPrices() throws Exception {
        // Given
        validDto.setPrecio(500000);
        String jsonWithHighPrice = objectMapper.writeValueAsString(validDto);

        doNothing().when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), any(Long.class));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithHighPrice))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /platos - Debería usar el propietario-id del header")
    void shouldUsePropietarioIdFromHeader() throws Exception {
        // Given
        Long specificPropietarioId = 42L;
        doNothing().when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), eq(specificPropietarioId));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", specificPropietarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated());

        verify(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), eq(specificPropietarioId));
    }

    @Test
    @DisplayName("POST /platos - Debería aceptar descripciones largas")
    void shouldAcceptLongDescriptions() throws Exception {
        // Given
        validDto.setDescripcion("Esta es una descripción muy larga del plato que incluye " +
                "todos los ingredientes, la forma de preparación y los beneficios nutricionales " +
                "del producto para que el cliente tenga toda la información necesaria.");
        String jsonWithLongDescription = objectMapper.writeValueAsString(validDto);

        doNothing().when(crearPlatoHandler).handle(any(CrearPlatoRequestDto.class), any(Long.class));

        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithLongDescription))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /platos - Debería validar Content-Type")
    void shouldValidateContentType() throws Exception {
        // When & Then
        mockMvc.perform(post("/platos")
                        .header("propietario-id", 1L)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(validJson))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("PUT /platos/{id} - Debería modificar plato exitosamente y retornar 200")
    void shouldUpdateDishSuccessfully() throws Exception {
        Long propietarioId = 1L;
        Long platoId = 10L;

        doNothing().when(actualizarPlatoHandler).handle(eq(platoId), any(ActualizarPlatoRequestDto.class), eq(propietarioId));

        mockMvc.perform(put("/platos/{id}", platoId)
                        .header("propietario-id", propietarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());

        verify(actualizarPlatoHandler).handle(eq(platoId), any(ActualizarPlatoRequestDto.class), eq(propietarioId));
    }

    @Test
    @DisplayName("PUT /platos/{id} - Debería retornar 400 cuando el plato no existe")
    void shouldReturn400WhenDishDoesNotExistOnUpdate() throws Exception {
        Long propietarioId = 1L;
        Long platoId = 999L;

        doThrow(new DominioException("El plato no existe"))
                .when(actualizarPlatoHandler).handle(eq(platoId), any(ActualizarPlatoRequestDto.class), eq(propietarioId));

        mockMvc.perform(put("/platos/{id}", platoId)
                        .header("propietario-id", propietarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /platos/{id} - Debería retornar 400 cuando el precio es inválido")
    void shouldReturn400WhenPriceInvalidOnUpdate() throws Exception {
        Long propietarioId = 1L;
        Long platoId = 10L;

        ActualizarPlatoRequestDto invalid = new ActualizarPlatoRequestDto(0, "desc");
        String invalidJson = objectMapper.writeValueAsString(invalid);

        doThrow(new DominioException("El precio debe ser mayor a cero"))
                .when(actualizarPlatoHandler).handle(eq(platoId), any(ActualizarPlatoRequestDto.class), eq(propietarioId));

        mockMvc.perform(put("/platos/{id}", platoId)
                        .header("propietario-id", propietarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}