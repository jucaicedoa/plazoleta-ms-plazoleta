package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.ActualizarPlatoRequestDto;
import com.plazoleta.plazoleta.domain.api.PlatoUpdateServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActualizarPlatoHandlerTest {

    @Mock
    private PlatoUpdateServicePort platoUpdateServicePort;

    @InjectMocks
    private ActualizarPlatoHandler actualizarPlatoHandler;

    private ActualizarPlatoRequestDto dto;

    @BeforeEach
    void setUp() {
        dto = new ActualizarPlatoRequestDto(18000, "Hamburguesa artesanal con queso y tocineta");
    }

    @Test
    @DisplayName("Debería delegar la modificación al servicio de dominio")
    void shouldDelegateUpdateToDomainService() {
        Long platoId = 10L;
        Long propietarioId = 1L;

        actualizarPlatoHandler.handle(platoId, dto, propietarioId);

        verify(platoUpdateServicePort).updateDish(platoId, dto.getPrecio(), dto.getDescripcion(), propietarioId);
    }
}

