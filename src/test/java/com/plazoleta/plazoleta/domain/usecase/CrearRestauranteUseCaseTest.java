package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.model.UsuarioModelo;
import com.plazoleta.plazoleta.domain.spi.RestaurantePersistencePort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - CreateRestaurantUseCase")
class CreateRestaurantUseCaseTest {

    @Mock
    private RestaurantePersistencePort persistencePort;

    @Mock
    private UsuarioValidationPort userValidationPort;

    private CrearRestauranteUseCase useCase;

    private Restaurante validRestaurant;
    private UsuarioModelo propietarioUser;

    @BeforeEach
    void setUp() {
        useCase = new CrearRestauranteUseCase(persistencePort, userValidationPort);

        // Restaurante válido para reutilizar en los tests
        validRestaurant = new Restaurante();
        validRestaurant.setNombre("La Arepa Feliz");
        validRestaurant.setDireccion("Calle 123 #45-67");
        validRestaurant.setPropietarioId(1L);
        validRestaurant.setTelefono("+573001234567");
        validRestaurant.setUrlLogo("https://ejemplo.com/logo.png");
        validRestaurant.setNit("900123456");

        // Usuario propietario válido
        propietarioUser = new UsuarioModelo();
        propietarioUser.setId(1L);
        propietarioUser.setRole("PROPIETARIO");
    }

    @Test
    @DisplayName("Debe crear restaurante exitosamente con datos válidos")
    void shouldCreateRestaurantSuccessfully() {
        // Arrange
        when(userValidationPort.getUserById(1L)).thenReturn(propietarioUser);

        // Act
        useCase.crearRestaurante(validRestaurant);

        // Assert
        verify(userValidationPort, times(1)).getUserById(1L);
        verify(persistencePort, times(1)).save(validRestaurant);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "  "})
    @DisplayName("Debe lanzar excepción cuando el nombre es null, vacío o solo espacios")
    void shouldThrowExceptionWhenNameIsNullOrEmpty(String nombre) {
        // Arrange
        validRestaurant.setNombre(nombre);

        // Act & Assert
        DominioException exception = assertThrows(
                DominioException.class,
                () -> useCase.crearRestaurante(validRestaurant)
        );

        assertEquals("El nombre del restaurante es obligatorio", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el nombre contiene solo números")
    void shouldThrowExceptionWhenNameIsOnlyNumbers() {
        // Arrange
        validRestaurant.setNombre("12345");

        // Act & Assert
        DominioException exception = assertThrows(
                DominioException.class,
                () -> useCase.crearRestaurante(validRestaurant)
        );

        assertEquals("El nombre del restaurante no puede contener solo números", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debe permitir nombre con números y letras")
    void shouldAllowNameWithNumbersAndLetters() {
        // Arrange
        validRestaurant.setNombre("Restaurante 123");
        when(userValidationPort.getUserById(1L)).thenReturn(propietarioUser);

        // Act
        useCase.crearRestaurante(validRestaurant);

        // Assert
        verify(persistencePort, times(1)).save(validRestaurant);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando NIT es null")
    void shouldThrowExceptionWhenNitIsNull() {
        // Arrange
        validRestaurant.setNit(null);

        // Act & Assert
        DominioException exception = assertThrows(
                DominioException.class,
                () -> useCase.crearRestaurante(validRestaurant)
        );

        assertEquals("El NIT debe ser numérico", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando NIT no es numérico")
    void shouldThrowExceptionWhenNitIsNotNumeric() {
        // Arrange
        validRestaurant.setNit("900-123-456");

        // Act & Assert
        DominioException exception = assertThrows(
                DominioException.class,
                () -> useCase.crearRestaurante(validRestaurant)
        );

        assertEquals("El NIT debe ser numérico", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"300-123-4567", "abc123", "++57300"})
    @DisplayName("Debe lanzar excepción cuando teléfono es null, vacío o no numérico")
    void shouldThrowExceptionWhenPhoneIsInvalid(String telefono) {
        // Arrange
        validRestaurant.setTelefono(telefono);

        // Act & Assert
        DominioException exception = assertThrows(
                DominioException.class,
                () -> useCase.crearRestaurante(validRestaurant)
        );

        assertEquals("El teléfono debe ser numérico y puede incluir el símbolo + al inicio", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debe permitir teléfono con símbolo + al inicio")
    void shouldAllowPhoneWithPlusSign() {
        // Arrange
        validRestaurant.setTelefono("+573001234567");
        when(userValidationPort.getUserById(1L)).thenReturn(propietarioUser);

        // Act
        useCase.crearRestaurante(validRestaurant);

        // Assert
        verify(persistencePort, times(1)).save(validRestaurant);
    }

    @Test
    @DisplayName("Debe permitir teléfono sin símbolo +")
    void shouldAllowPhoneWithoutPlusSign() {
        // Arrange
        validRestaurant.setTelefono("3001234567");
        when(userValidationPort.getUserById(1L)).thenReturn(propietarioUser);

        // Act
        useCase.crearRestaurante(validRestaurant);

        // Assert
        verify(persistencePort, times(1)).save(validRestaurant);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando teléfono excede 13 caracteres")
    void shouldThrowExceptionWhenPhoneExceeds13Characters() {
        // Arrange
        validRestaurant.setTelefono("+57300123456789"); // 15 caracteres

        // Act & Assert
        DominioException exception = assertThrows(
                DominioException.class,
                () -> useCase.crearRestaurante(validRestaurant)
        );

        assertEquals("El teléfono debe tener máximo 13 caracteres", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario propietario no existe")
    void shouldThrowExceptionWhenOwnerUserDoesNotExist() {
        // Arrange
        when(userValidationPort.getUserById(1L)).thenReturn(null);

        // Act & Assert
        DominioException exception = assertThrows(
                DominioException.class,
                () -> useCase.crearRestaurante(validRestaurant)
        );

        assertEquals("El usuario propietario no existe", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario no tiene rol PROPIETARIO")
    void shouldThrowExceptionWhenUserIsNotOwner() {
        // Arrange
        UsuarioModelo clienteUser = new UsuarioModelo();
        clienteUser.setId(1L);
        clienteUser.setRole("CLIENTE");
        when(userValidationPort.getUserById(1L)).thenReturn(clienteUser);

        // Act & Assert
        DominioException exception = assertThrows(
                DominioException.class,
                () -> useCase.crearRestaurante(validRestaurant)
        );

        assertEquals("El usuario no tiene rol de propietario", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"PROPIETARIO", "propietario", "2"})
    @DisplayName("Debe aceptar roles válidos de propietario en diferentes formatos")
    void shouldAcceptValidOwnerRoles(String role) {
        // Arrange
        propietarioUser.setRole(role);
        when(userValidationPort.getUserById(1L)).thenReturn(propietarioUser);

        // Act
        useCase.crearRestaurante(validRestaurant);

        // Assert
        verify(persistencePort, times(1)).save(validRestaurant);
    }

    @Test
    @DisplayName("No debe aceptar otros IDs de rol numéricos")
    void shouldNotAcceptOtherNumericRoleIds() {
        // Arrange
        UsuarioModelo otroRolUser = new UsuarioModelo();
        otroRolUser.setId(1L);
        otroRolUser.setRole("1"); // ID de otro rol
        when(userValidationPort.getUserById(1L)).thenReturn(otroRolUser);

        // Act & Assert
        DominioException exception = assertThrows(
                DominioException.class,
                () -> useCase.crearRestaurante(validRestaurant)
        );

        assertEquals("El usuario no tiene rol de propietario", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }
}