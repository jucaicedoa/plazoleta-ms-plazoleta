package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.exception.RolNoAutorizadoException;
import com.plazoleta.plazoleta.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.model.UsuarioModelo;
import com.plazoleta.plazoleta.domain.spi.PlatoPersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestauranteValidationPort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarPlatoUseCaseTest {

    @Mock
    private PlatoPersistencePort dishPersistencePort;

    @Mock
    private UsuarioValidationPort userValidationPort;

    @Mock
    private RestauranteValidationPort restaurantValidationPort;

    private ActualizarPlatoUseCase updateDishUseCase;

    private Long propietarioId;
    private Long platoId;
    private Plato platoExistente;
    private UsuarioModelo usuarioPropietario;

    @BeforeEach
    void setUp() {
        updateDishUseCase = new ActualizarPlatoUseCase(dishPersistencePort, userValidationPort, restaurantValidationPort);

        propietarioId = 1L;
        platoId = 10L;

        usuarioPropietario = new UsuarioModelo(propietarioId, "PROPIETARIO");

        platoExistente = new Plato(
                "Hamburguesa Especial",
                25000,
                "Carne artesanal",
                "http://img.com/burger.png",
                "COMIDA_RAPIDA",
                99L
        );
        platoExistente.setId(platoId);
        platoExistente.setActivo(true);
    }

    @Test
    @DisplayName("Debería modificar precio y descripción y guardar el plato")
    void shouldUpdateDishPriceAndDescription() {
        // Given
        when(userValidationPort.getUserById(propietarioId)).thenReturn(usuarioPropietario);
        when(dishPersistencePort.getById(platoId)).thenReturn(platoExistente);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(platoExistente.getRestauranteId(), propietarioId)).thenReturn(true);

        Integer nuevoPrecio = 18000;
        String nuevaDescripcion = "Hamburguesa artesanal con queso y tocineta";

        // When
        updateDishUseCase.updateDish(platoId, nuevoPrecio, nuevaDescripcion, propietarioId);

        // Then
        ArgumentCaptor<Plato> captor = ArgumentCaptor.forClass(Plato.class);
        verify(dishPersistencePort).save(captor.capture());

        Plato guardado = captor.getValue();
        assertNotNull(guardado);
        assertEquals(platoId, guardado.getId());
        assertEquals(nuevoPrecio, guardado.getPrecio());
        assertEquals(nuevaDescripcion, guardado.getDescripcion());
        // No debe tocar nombre/restauranteId/activo
        assertEquals("Hamburguesa Especial", guardado.getNombre());
        assertEquals(99L, guardado.getRestauranteId());
        assertEquals(true, guardado.getActivo());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe")
    void shouldThrowIfUserDoesNotExist() {
        when(userValidationPort.getUserById(propietarioId))
                .thenThrow(new UsuarioNoEncontradoException("El usuario no existe"));

        UsuarioNoEncontradoException ex = assertThrows(UsuarioNoEncontradoException.class,
                () -> updateDishUseCase.updateDish(platoId, 18000, "desc", propietarioId));

        assertEquals("El usuario no existe", ex.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no es propietario")
    void shouldThrowIfUserIsNotOwner() {
        when(userValidationPort.getUserById(propietarioId)).thenReturn(new UsuarioModelo(propietarioId, "CLIENTE"));

        RolNoAutorizadoException ex = assertThrows(RolNoAutorizadoException.class,
                () -> updateDishUseCase.updateDish(platoId, 18000, "desc", propietarioId));

        assertEquals("El usuario no tiene el rol de propietario", ex.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería aceptar rol propietario como '2'")
    void shouldAcceptOwnerRoleAs2() {
        when(userValidationPort.getUserById(propietarioId)).thenReturn(new UsuarioModelo(propietarioId, "2"));
        when(dishPersistencePort.getById(platoId)).thenReturn(platoExistente);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(platoExistente.getRestauranteId(), propietarioId)).thenReturn(true);

        updateDishUseCase.updateDish(platoId, 18000, "Nueva desc", propietarioId);

        verify(dishPersistencePort).save(any(Plato.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción si el plato no existe")
    void shouldThrowIfDishDoesNotExist() {
        when(userValidationPort.getUserById(propietarioId)).thenReturn(usuarioPropietario);
        when(dishPersistencePort.getById(platoId)).thenReturn(null);

        DominioException ex = assertThrows(DominioException.class,
                () -> updateDishUseCase.updateDish(platoId, 18000, "desc", propietarioId));

        assertEquals("El plato no existe", ex.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el restaurante no pertenece al propietario")
    void shouldThrowIfRestaurantNotBelongsToOwner() {
        when(userValidationPort.getUserById(propietarioId)).thenReturn(usuarioPropietario);
        when(dishPersistencePort.getById(platoId)).thenReturn(platoExistente);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(platoExistente.getRestauranteId(), propietarioId)).thenReturn(false);

        DominioException ex = assertThrows(DominioException.class,
                () -> updateDishUseCase.updateDish(platoId, 18000, "desc", propietarioId));

        assertEquals("El restaurante no pertenece al propietario", ex.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el precio es nulo o <= 0")
    void shouldThrowIfPriceInvalid() {
        when(userValidationPort.getUserById(propietarioId)).thenReturn(usuarioPropietario);
        when(dishPersistencePort.getById(platoId)).thenReturn(platoExistente);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(platoExistente.getRestauranteId(), propietarioId)).thenReturn(true);

        DominioException ex1 = assertThrows(DominioException.class,
                () -> updateDishUseCase.updateDish(platoId, null, "desc", propietarioId));
        assertEquals("El precio debe ser mayor a cero", ex1.getMessage());

        DominioException ex2 = assertThrows(DominioException.class,
                () -> updateDishUseCase.updateDish(platoId, 0, "desc", propietarioId));
        assertEquals("El precio debe ser mayor a cero", ex2.getMessage());

        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si la descripción es nula o vacía")
    void shouldThrowIfDescriptionInvalid() {
        when(userValidationPort.getUserById(propietarioId)).thenReturn(usuarioPropietario);
        when(dishPersistencePort.getById(platoId)).thenReturn(platoExistente);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(platoExistente.getRestauranteId(), propietarioId)).thenReturn(true);

        DominioException ex1 = assertThrows(DominioException.class,
                () -> updateDishUseCase.updateDish(platoId, 18000, null, propietarioId));
        assertEquals("La descripción del plato es obligatoria", ex1.getMessage());

        DominioException ex2 = assertThrows(DominioException.class,
                () -> updateDishUseCase.updateDish(platoId, 18000, "   ", propietarioId));
        assertEquals("La descripción del plato es obligatoria", ex2.getMessage());

        verify(dishPersistencePort, never()).save(any());
    }
}