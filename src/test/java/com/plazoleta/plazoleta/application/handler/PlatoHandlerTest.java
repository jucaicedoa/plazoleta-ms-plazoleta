package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.ActualizarPlatoRequestDto;
import com.plazoleta.plazoleta.application.dto.CrearPlatoRequestDto;
import com.plazoleta.plazoleta.application.mapper.PlatoApplicationMapper;
import com.plazoleta.plazoleta.application.security.ICurrentUserProvider;
import com.plazoleta.plazoleta.domain.api.PlatoServicePort;
import com.plazoleta.plazoleta.domain.api.PlatoUpdateServicePort;
import com.plazoleta.plazoleta.domain.model.Plato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatoHandlerTest {

    @Mock
    private PlatoServicePort platoServicePort;
    @Mock
    private PlatoUpdateServicePort platoUpdateServicePort;
    @Mock
    private PlatoApplicationMapper mapper;
    @Mock
    private ICurrentUserProvider currentUserProvider;
    @InjectMocks
    private PlatoHandler platoHandler;

    private CrearPlatoRequestDto crearPlatoRequestDto;
    private ActualizarPlatoRequestDto actualizarPlatoRequestDto;
    private Plato platoValido;
    private Long propietarioId;

    @BeforeEach
    void setUp() {
        propietarioId = 1L;
        crearPlatoRequestDto = new CrearPlatoRequestDto();
        crearPlatoRequestDto.setNombre("Hamburguesa Especial");
        crearPlatoRequestDto.setPrecio(25000);
        crearPlatoRequestDto.setDescripcion("Carne artesanal con queso cheddar");
        crearPlatoRequestDto.setUrlImagen("http://img.com/burger.png");
        crearPlatoRequestDto.setCategoria("COMIDA_RAPIDA");
        crearPlatoRequestDto.setRestauranteId(10L);
        actualizarPlatoRequestDto = new ActualizarPlatoRequestDto(18000, "Hamburguesa artesanal con queso y tocineta");
        platoValido = new Plato("Hamburguesa Especial", 25000, "Carne artesanal con queso cheddar",
                "http://img.com/burger.png", "COMIDA_RAPIDA", 10L);
    }

    @Test
    @DisplayName("createDish - Deberia delegar al servicio de dominio")
    void createDishShouldDelegateToServicePort() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(propietarioId);
        when(mapper.toDomain(crearPlatoRequestDto)).thenReturn(platoValido);
        platoHandler.createDish(crearPlatoRequestDto);
        verify(mapper).toDomain(crearPlatoRequestDto);
        verify(platoServicePort).crearPlato(platoValido, propietarioId);
    }

    @Test
    @DisplayName("updateDish - Deberia delegar al servicio de dominio")
    void updateDishShouldDelegateToDomainService() {
        Long platoId = 10L;
        when(currentUserProvider.getCurrentUserId()).thenReturn(propietarioId);
        platoHandler.updateDish(platoId, actualizarPlatoRequestDto);
        verify(platoUpdateServicePort).updateDish(platoId, actualizarPlatoRequestDto.getPrecio(),
                actualizarPlatoRequestDto.getDescripcion(), propietarioId);
    }
}