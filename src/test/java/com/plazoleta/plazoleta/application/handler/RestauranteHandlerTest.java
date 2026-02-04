package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.application.mapper.RestauranteApplicationMapper;
import com.plazoleta.plazoleta.domain.api.RestauranteServicePort;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - RestauranteHandler")
class RestauranteHandlerTest {

    @Mock
    private RestauranteServicePort servicePort;

    @Mock
    private RestauranteApplicationMapper mapper;

    @InjectMocks
    private RestauranteHandler handler;

    private CrearRestauranteRequestDto requestDto;
    private Restaurante restaurantDomain;

    @BeforeEach
    void setUp() {
        requestDto = new CrearRestauranteRequestDto();
        requestDto.setNombre("La Arepa Feliz");
        requestDto.setDireccion("Calle 123 #45-67");
        requestDto.setPropietarioId(1L);
        requestDto.setTelefono("+573001234567");
        requestDto.setUrlLogo("https://ejemplo.com/logo.png");
        requestDto.setNit("900123456");

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
        when(mapper.toDomain(requestDto)).thenReturn(restaurantDomain);

        handler.createRestaurante(requestDto);

        verify(mapper, times(1)).toDomain(requestDto);
        verify(servicePort, times(1)).crearRestaurante(restaurantDomain);
    }

    @Test
    @DisplayName("Debe pasar el modelo de dominio correcto al service port")
    void shouldPassCorrectDomainModelToServicePort() {
        when(mapper.toDomain(requestDto)).thenReturn(restaurantDomain);

        handler.createRestaurante(requestDto);

        verify(servicePort).crearRestaurante(restaurantDomain);
        verifyNoMoreInteractions(servicePort);
    }

    @Test
    @DisplayName("No debe llamar al service port si el mapper falla")
    void shouldNotCallServicePortIfMapperFails() {
        when(mapper.toDomain(requestDto)).thenThrow(new RuntimeException("Mapping error"));

        try {
            handler.createRestaurante(requestDto);
        } catch (RuntimeException e) {
            // Esperado
        }

        verify(mapper, times(1)).toDomain(requestDto);
        verify(servicePort, never()).crearRestaurante(any());
    }
}