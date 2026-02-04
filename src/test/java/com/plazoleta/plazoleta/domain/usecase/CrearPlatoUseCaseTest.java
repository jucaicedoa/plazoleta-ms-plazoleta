package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.exception.DominioException;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearPlatoUseCaseTest {

    @Mock
    private PlatoPersistencePort dishPersistencePort;

    @Mock
    private UsuarioValidationPort userValidationPort;

    @Mock
    private RestauranteValidationPort restaurantValidationPort;

    private CrearPlatoUseCase createDishUseCase;

    private Plato validDish;
    private Long validPropietarioId;
    private UsuarioModelo ownerUser;

    @BeforeEach
    void setUp() {
        createDishUseCase = new CrearPlatoUseCase(
                dishPersistencePort,
                userValidationPort,
                restaurantValidationPort
        );

        validPropietarioId = 1L;

        ownerUser = new UsuarioModelo();
        ownerUser.setId(validPropietarioId);
        ownerUser.setRole("PROPIETARIO");

        validDish = new Plato(
                "Hamburguesa Especial",
                25000,
                "Carne artesanal con queso cheddar",
                "http://img.com/burger.png",
                "COMIDA_RAPIDA",
                10L
        );
    }

    @Test
    @DisplayName("Debería crear un plato exitosamente cuando todos los datos son válidos")
    void shouldCreateDishSuccessfully() {
        // Given
        when(userValidationPort.getUserById(validPropietarioId)).thenReturn(ownerUser);
        when(restaurantValidationPort.restauranteExiste(validDish.getRestauranteId())).thenReturn(true);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(validDish.getRestauranteId(), validPropietarioId))
                .thenReturn(true);

        // When
        createDishUseCase.crearPlato(validDish, validPropietarioId);

        // Then
        ArgumentCaptor<Plato> dishCaptor = ArgumentCaptor.forClass(Plato.class);
        verify(dishPersistencePort).save(dishCaptor.capture());

        Plato savedDish = dishCaptor.getValue();
        assertNotNull(savedDish);
        assertEquals("Hamburguesa Especial", savedDish.getNombre());
        assertTrue(savedDish.getActivo(), "El plato debe estar activo por defecto");
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el usuario no existe")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userValidationPort.getUserById(validPropietarioId))
                .thenThrow(new UsuarioNoEncontradoException("El usuario no existe"));

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class, () ->
                createDishUseCase.crearPlato(validDish, validPropietarioId));

        assertEquals("El usuario no existe", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el usuario no es propietario")
    void shouldThrowExceptionWhenUserIsNotOwner() {
        // Given
        UsuarioModelo clientUser = new UsuarioModelo();
        clientUser.setId(validPropietarioId);
        clientUser.setRole("CLIENTE");

        when(userValidationPort.getUserById(validPropietarioId)).thenReturn(clientUser);

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("El usuario no tiene el rol de propietario", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el restaurante no existe")
    void shouldThrowExceptionWhenRestaurantDoesNotExist() {
        // Given
        when(userValidationPort.getUserById(validPropietarioId)).thenReturn(ownerUser);
        when(restaurantValidationPort.restauranteExiste(validDish.getRestauranteId())).thenReturn(false);

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("El restaurante especificado no existe", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el restaurante no pertenece al propietario")
    void shouldThrowExceptionWhenRestaurantDoesNotBelongToOwner() {
        // Given
        when(userValidationPort.getUserById(validPropietarioId)).thenReturn(ownerUser);
        when(restaurantValidationPort.restauranteExiste(validDish.getRestauranteId())).thenReturn(true);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(validDish.getRestauranteId(), validPropietarioId))
                .thenReturn(false);

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("El restaurante no pertenece al propietario", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el precio es cero")
    void shouldThrowExceptionWhenPriceIsZero() {
        // Given
        validDish.setPrecio(0);
        when(userValidationPort.getUserById(validPropietarioId)).thenReturn(ownerUser);
        when(restaurantValidationPort.restauranteExiste(validDish.getRestauranteId())).thenReturn(true);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(validDish.getRestauranteId(), validPropietarioId))
                .thenReturn(true);

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("El precio debe ser mayor a cero", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el precio es negativo")
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Given
        validDish.setPrecio(-5000);
        when(userValidationPort.getUserById(validPropietarioId)).thenReturn(ownerUser);
        when(restaurantValidationPort.restauranteExiste(validDish.getRestauranteId())).thenReturn(true);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(validDish.getRestauranteId(), validPropietarioId))
                .thenReturn(true);

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("El precio debe ser mayor a cero", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el nombre está vacío")
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Given
        validDish.setNombre("");

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("El nombre del plato es obligatorio", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la descripción está vacía")
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        // Given
        validDish.setDescripcion("");

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("La descripción del plato es obligatoria", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la URL de imagen está vacía")
    void shouldThrowExceptionWhenImageUrlIsEmpty() {
        // Given
        validDish.setUrlImagen("");

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("La URL de la imagen es obligatoria", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la categoría está vacía")
    void shouldThrowExceptionWhenCategoryIsEmpty() {
        // Given
        validDish.setCategoria("");

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("La categoría del plato es obligatoria", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el restauranteId es nulo")
    void shouldThrowExceptionWhenRestaurantIdIsNull() {
        // Given
        validDish.setRestauranteId(null);

        // When & Then
        DominioException exception = assertThrows(DominioException.class, () -> {
            createDishUseCase.crearPlato(validDish, validPropietarioId);
        });

        assertEquals("El ID del restaurante es obligatorio", exception.getMessage());
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debería establecer activo como true si es null")
    void shouldSetActiveToTrueIfNull() {
        // Given
        validDish.setActivo(null);
        when(userValidationPort.getUserById(validPropietarioId)).thenReturn(ownerUser);
        when(restaurantValidationPort.restauranteExiste(validDish.getRestauranteId())).thenReturn(true);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(validDish.getRestauranteId(), validPropietarioId))
                .thenReturn(true);

        // When
        createDishUseCase.crearPlato(validDish, validPropietarioId);

        // Then
        ArgumentCaptor<Plato> dishCaptor = ArgumentCaptor.forClass(Plato.class);
        verify(dishPersistencePort).save(dishCaptor.capture());

        Plato savedDish = dishCaptor.getValue();
        assertTrue(savedDish.getActivo(), "El plato debe estar activo si activo era null");
    }

    @Test
    @DisplayName("Debería validar el rol en mayúsculas y minúsculas")
    void shouldValidateRoleCaseInsensitive() {
        // Given
        UsuarioModelo ownerLowerCase = new UsuarioModelo();
        ownerLowerCase.setId(validPropietarioId);
        ownerLowerCase.setRole("propietario");

        when(userValidationPort.getUserById(validPropietarioId)).thenReturn(ownerLowerCase);
        when(restaurantValidationPort.restauranteExiste(validDish.getRestauranteId())).thenReturn(true);
        when(restaurantValidationPort.restaurantePerteneceAPropietario(validDish.getRestauranteId(), validPropietarioId))
                .thenReturn(true);

        // When
        createDishUseCase.crearPlato(validDish, validPropietarioId);

        // Then
        verify(dishPersistencePort).save(any(Plato.class));
    }
}