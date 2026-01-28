package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.CrearPlatoRequestDto;
import com.plazoleta.plazoleta.application.mapper.PlatoApplicationMapper;
import com.plazoleta.plazoleta.domain.api.PlatoServicePort;
import com.plazoleta.plazoleta.domain.model.Plato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearPlatoHandlerTest {

    @Mock
    private PlatoServicePort platoServicePort;

    @Mock
    private PlatoApplicationMapper mapper;

    @InjectMocks
    private CrearPlatoHandler crearPlatoHandler;

    private CrearPlatoRequestDto crearPlatoRequestDto;
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

        platoValido = new Plato(
                        "Hamburguesa Especial",
                        25000,
                        "Carne artesanal con queso cheddar",
                        "http://img.com/burger.png",
                        "COMIDA_RAPIDA",
                        10L
                );
    }

    @Test
    @DisplayName("Debería delegar correctamente al servicio de dominio")
    void shouldDelegateToServicePort() {
        // Given
        when(mapper.toDomain(crearPlatoRequestDto)).thenReturn(platoValido);

        // When
        crearPlatoHandler.handle(crearPlatoRequestDto, propietarioId);

        // Then
        verify(mapper).toDomain(crearPlatoRequestDto);
        // Asumo que el método en el puerto es 'crearPlato' o 'guardarPlato'
        verify(platoServicePort).crearPlato(platoValido, propietarioId);
    }

    @Test
    @DisplayName("Debería mapear correctamente el DTO a dominio")
    void shouldMapDtoToDomain() {
        // Given
        when(mapper.toDomain(crearPlatoRequestDto)).thenReturn(platoValido);

        // When
        crearPlatoHandler.handle(crearPlatoRequestDto, propietarioId);

        // Then
        verify(mapper).toDomain(crearPlatoRequestDto);
    }

    @Test
    @DisplayName("Debería pasar el propietarioId al servicio")
    void shouldPassPropietarioIdToService() {
        // Given
        Long specificPropietarioId = 42L;
        when(mapper.toDomain(crearPlatoRequestDto)).thenReturn(platoValido);

        // When
        crearPlatoHandler.handle(crearPlatoRequestDto, specificPropietarioId);

        // Then
        verify(platoServicePort).crearPlato(platoValido, specificPropietarioId);
    }

    @Test
    @DisplayName("Debería manejar diferentes tipos de platos")
    void shouldHandleDifferentDishTypes() {
        // Given
        CrearPlatoRequestDto pizzaDto = new CrearPlatoRequestDto();
        pizzaDto.setNombre("Pizza Margarita");
        pizzaDto.setPrecio(35000);
        pizzaDto.setDescripcion("Pizza italiana tradicional");
        pizzaDto.setUrlImagen("http://img.com/pizza.png");
        pizzaDto.setCategoria("PIZZA");
        pizzaDto.setRestauranteId(10L);

        Plato platoPizza = new Plato(
                        "Hamburguesa Especial",
                        25000,
                        "Carne artesanal con queso cheddar",
                        "http://img.com/burger.png",
                        "COMIDA_RAPIDA",
                        10L
                );

        when(mapper.toDomain(pizzaDto)).thenReturn(platoPizza);

        // When
        crearPlatoHandler.handle(pizzaDto, propietarioId);

        // Then
        verify(mapper).toDomain(pizzaDto);
        verify(platoServicePort).crearPlato(platoPizza, propietarioId);
    }

    @Test
    @DisplayName("Debería manejar platos con precios altos")
    void shouldHandleHighPricedDishes() {
        // Given
        crearPlatoRequestDto.setPrecio(150000);
        platoValido.setPrecio(150000);

        when(mapper.toDomain(crearPlatoRequestDto)).thenReturn(platoValido);

        // When
        crearPlatoHandler.handle(crearPlatoRequestDto, propietarioId);

        // Then
        verify(platoServicePort).crearPlato(any(Plato.class), eq(propietarioId));
    }

    @Test
    @DisplayName("Debería manejar diferentes restaurantes")
    void shouldHandleDifferentRestaurants() {
        // Given
        Long differentRestaurantId = 99L;
        crearPlatoRequestDto.setRestauranteId(differentRestaurantId);
        platoValido.setRestauranteId(differentRestaurantId);

        when(mapper.toDomain(crearPlatoRequestDto)).thenReturn(platoValido);

        // When
        crearPlatoHandler.handle(crearPlatoRequestDto, propietarioId);

        // Then
        verify(mapper).toDomain(crearPlatoRequestDto);
        verify(platoServicePort).crearPlato(platoValido, propietarioId);
    }
}