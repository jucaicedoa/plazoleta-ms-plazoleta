package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.application.mapper.RestauranteApplicationMapper;
import com.plazoleta.plazoleta.domain.api.RestauranteServicePort;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - CreateRestaurantHandler")
class CrearRestauranteHandlerTest {

    @Mock
    private RestauranteServicePort servicePort;

    @Mock
    private RestauranteApplicationMapper mapper;

    private CrearRestauranteHandler handler;

    private CrearRestauranteRequestDto requestDto;
    private Restaurante restaurantDomain;

    @BeforeEach
    void setUp() {
        handler = new CrearRestauranteHandler(servicePort, mapper);

        // DTO de entrada
        requestDto = new CrearRestauranteRequestDto();
        requestDto.setNombre("La Arepa Feliz");
        requestDto.setDireccion("Calle 123 #45-67");
        requestDto.setPropietarioId(1L);
        requestDto.setTelefono("+573001234567");
        requestDto.setUrlLogo("https://ejemplo.com/logo.png");
        requestDto.setNit("900123456");

        // Modelo de dominio
        restaurantDomain = new Restaurante();
        restaurantDomain.setNombre("La Arepa Feliz");
        restaurantDomain.setDireccion("Calle 123 #45-67");
        restaurantDomain.setPropietarioId(1L);
        restaurantDomain.setTelefono("+573001234567");
        restaurantDomain.setUrlLogo("https://ejemplo.com/logo.png");
        restaurantDomain.setNit("900123456");
    }

    @Test
    @DisplayName("Debe delegar correctamente al mapper y al service port")
    void shouldDelegateToMapperAndServicePort() {
        // Arrange
        when(mapper.toDomain(requestDto)).thenReturn(restaurantDomain);

        // Act
        handler.handle(requestDto);

        // Assert
        verify(mapper, times(1)).toDomain(requestDto);
        verify(servicePort, times(1)).crearRestaurante(restaurantDomain);
    }

    @Test
    @DisplayName("Debe pasar el DTO correcto al mapper")
    void shouldPassCorrectDtoToMapper() {
        // Arrange
        when(mapper.toDomain(requestDto)).thenReturn(restaurantDomain);

        // Act
        handler.handle(requestDto);

        // Assert
        verify(mapper).toDomain(requestDto);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    @DisplayName("Debe pasar el modelo de dominio correcto al service port")
    void shouldPassCorrectDomainModelToServicePort() {
        // Arrange
        when(mapper.toDomain(requestDto)).thenReturn(restaurantDomain);

        // Act
        handler.handle(requestDto);

        // Assert
        verify(servicePort).crearRestaurante(restaurantDomain);
        verifyNoMoreInteractions(servicePort);
    }

    @Test
    @DisplayName("No debe llamar al service port si el mapper falla")
    void shouldNotCallServicePortIfMapperFails() {
        // Arrange
        when(mapper.toDomain(requestDto)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        try {
            handler.handle(requestDto);
        } catch (RuntimeException e) {
            // Esperado
        }

        verify(mapper, times(1)).toDomain(requestDto);
        verify(servicePort, never()).crearRestaurante(any());
    }

    @Test
    @DisplayName("Debe manejar múltiples llamadas independientes")
    void shouldHandleMultipleIndependentCalls() {
        // Arrange
        when(mapper.toDomain(any(CrearRestauranteRequestDto.class))).thenReturn(restaurantDomain);

        CrearRestauranteRequestDto dto1 = new CrearRestauranteRequestDto();
        dto1.setNombre("Restaurante 1");
        dto1.setNit("111111111");

        CrearRestauranteRequestDto dto2 = new CrearRestauranteRequestDto();
        dto2.setNombre("Restaurante 2");
        dto2.setNit("222222222");

        // Act
        handler.handle(dto1);
        handler.handle(dto2);

        // Assert
        verify(mapper, times(2)).toDomain(any(CrearRestauranteRequestDto.class));
        verify(servicePort, times(2)).crearRestaurante(any(Restaurante.class));
    }
}