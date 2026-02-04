package com.plazoleta.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta.application.dto.CrearPlatoRequestDto;
import com.plazoleta.plazoleta.application.dto.ActualizarPlatoRequestDto;
import com.plazoleta.plazoleta.application.handler.IPlatoHandler;
import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.exception.PlatoNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.RestauranteNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.RestauranteNoPerteneceException;
import com.plazoleta.plazoleta.domain.exception.RolNoAutorizadoException;
import com.plazoleta.plazoleta.infraestructure.input.rest.PlatoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc(addFilters = false)
class PlatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private IPlatoHandler platoHandler;

    private static final String BASE_URL = "/api/v1/platos";

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
        doNothing().when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated());

        verify(platoHandler).createDish(any(CrearPlatoRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/platos - Debería retornar 400 cuando faltan datos obligatorios")
    void shouldReturn400WhenMissingRequiredFields() throws Exception {
        // Given
        CrearPlatoRequestDto invalidDto = new CrearPlatoRequestDto();
        invalidDto.setNombre("");
        invalidDto.setPrecio(25000);
        invalidDto.setRestauranteId(10L);

        String invalidJson = objectMapper.writeValueAsString(invalidDto);

        doThrow(new DominioException("El nombre del plato es obligatorio"))
                .when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        mockMvc.perform(post(BASE_URL)
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
                .when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        // When & Then
        mockMvc.perform(post(BASE_URL)
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
                .when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNegativePrice))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/platos - Debería retornar 403 cuando el usuario no es propietario")
    void shouldReturn403WhenUserIsNotOwner() throws Exception {
        doThrow(new RolNoAutorizadoException("El usuario no tiene el rol de propietario"))
                .when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/v1/platos - Debería retornar 404 cuando el restaurante no existe")
    void shouldReturn404WhenRestaurantDoesNotExist() throws Exception {
        validDto.setRestauranteId(999L);
        String jsonWithInvalidRestaurant = objectMapper.writeValueAsString(validDto);

        doThrow(new RestauranteNoEncontradoException("El restaurante especificado no existe"))
                .when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithInvalidRestaurant))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/platos - Debería retornar 403 cuando el restaurante no pertenece al propietario")
    void shouldReturn403WhenRestaurantDoesNotBelongToOwner() throws Exception {
        doThrow(new RestauranteNoPerteneceException("El restaurante no pertenece al propietario"))
                .when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /platos - Debería aceptar diferentes categorías")
    void shouldAcceptDifferentCategories() throws Exception {
        // Given
        validDto.setCategoria("PIZZA");
        String jsonWithPizza = objectMapper.writeValueAsString(validDto);

        doNothing().when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        mockMvc.perform(post(BASE_URL)
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

        doNothing().when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithHighPrice))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/platos - Debería invocar createDish con el DTO")
    void shouldInvokeCreateDishWithDto() throws Exception {
        doNothing().when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated());

        verify(platoHandler).createDish(any(CrearPlatoRequestDto.class));
    }

    @Test
    @DisplayName("POST /platos - Debería aceptar descripciones largas")
    void shouldAcceptLongDescriptions() throws Exception {
        // Given
        validDto.setDescripcion("Esta es una descripción muy larga del plato que incluye " +
                "todos los ingredientes, la forma de preparación y los beneficios nutricionales " +
                "del producto para que el cliente tenga toda la información necesaria.");
        String jsonWithLongDescription = objectMapper.writeValueAsString(validDto);

        doNothing().when(platoHandler).createDish(any(CrearPlatoRequestDto.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithLongDescription))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/platos - No debe aceptar Content-Type text/plain")
    void shouldValidateContentType() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(validJson))
                .andExpect(result -> {
                    int s = result.getResponse().getStatus();
                    if (s == 201) {
                        throw new AssertionError("No se debe retornar 201 con Content-Type text/plain");
                    }
                });
    }

    @Test
    @DisplayName("PUT /platos/{id} - Debería modificar plato exitosamente y retornar 200")
    void shouldUpdateDishSuccessfully() throws Exception {
        Long platoId = 10L;

        doNothing().when(platoHandler).updateDish(eq(platoId), any(ActualizarPlatoRequestDto.class));

        mockMvc.perform(put(BASE_URL + "/{id}", platoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());

        verify(platoHandler).updateDish(eq(platoId), any(ActualizarPlatoRequestDto.class));
    }

    @Test
    @DisplayName("PUT /api/v1/platos/{id} - Debería retornar 404 cuando el plato no existe")
    void shouldReturn404WhenDishDoesNotExistOnUpdate() throws Exception {
        Long platoId = 999L;

        doThrow(new PlatoNoEncontradoException("El plato no existe"))
                .when(platoHandler).updateDish(eq(platoId), any(ActualizarPlatoRequestDto.class));

        mockMvc.perform(put(BASE_URL + "/{id}", platoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /platos/{id} - Debería retornar 400 cuando el precio es inválido")
    void shouldReturn400WhenPriceInvalidOnUpdate() throws Exception {
        Long platoId = 10L;

        ActualizarPlatoRequestDto invalid = new ActualizarPlatoRequestDto(0, "desc");
        String invalidJson = objectMapper.writeValueAsString(invalid);

        doThrow(new DominioException("El precio debe ser mayor a cero"))
                .when(platoHandler).updateDish(eq(platoId), any(ActualizarPlatoRequestDto.class));

        mockMvc.perform(put(BASE_URL + "/{id}", platoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}