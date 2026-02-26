package com.plazoleta.plazoleta.infrastructure.out.validation;

import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.infraestructure.out.validation.RestauranteBusinessValidationAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestauranteBusinessValidationAdapter")
class RestauranteBusinessValidationAdapterTest {

    private RestauranteBusinessValidationAdapter adapter;

    private Restaurante restauranteValido;

    @BeforeEach
    void setUp() {
        adapter = new RestauranteBusinessValidationAdapter();
        restauranteValido = new Restaurante();
        restauranteValido.setNombre("La Arepa Feliz");
        restauranteValido.setNit("900123456");
        restauranteValido.setTelefono("+573001234567");
        restauranteValido.setDireccion("Calle 123");
        restauranteValido.setPropietarioId(1L);
        restauranteValido.setUrlLogo("http://logo.com/logo.png");
    }

    @Test
    @DisplayName("No lanza excepción cuando el restaurante es válido")
    void shouldNotThrowWhenRestauranteIsValid() {
        assertDoesNotThrow(() -> adapter.validateRestaurante(restauranteValido));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "  "})
    @DisplayName("Lanza excepción cuando el nombre es null, vacío o solo espacios")
    void shouldThrowWhenNombreIsNullOrEmpty(String nombre) {
        restauranteValido.setNombre(nombre);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateRestaurante(restauranteValido));

        assertEquals("El nombre del restaurante es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Lanza excepción cuando el nombre contiene solo números")
    void shouldThrowWhenNombreIsOnlyNumbers() {
        restauranteValido.setNombre("12345");

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateRestaurante(restauranteValido));

        assertEquals("El nombre del restaurante no puede contener solo números", ex.getMessage());
    }

    @Test
    @DisplayName("No lanza cuando el nombre tiene letras y números")
    void shouldNotThrowWhenNombreHasLettersAndNumbers() {
        restauranteValido.setNombre("Restaurante 123");

        assertDoesNotThrow(() -> adapter.validateRestaurante(restauranteValido));
    }

    @Test
    @DisplayName("Lanza excepción cuando NIT es null")
    void shouldThrowWhenNitIsNull() {
        restauranteValido.setNit(null);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateRestaurante(restauranteValido));

        assertEquals("El NIT debe ser numérico", ex.getMessage());
    }

    @Test
    @DisplayName("Lanza excepción cuando NIT no es numérico")
    void shouldThrowWhenNitIsNotNumeric() {
        restauranteValido.setNit("900-123-456");

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateRestaurante(restauranteValido));

        assertEquals("El NIT debe ser numérico", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"300-123-4567", "abc123", "++57300"})
    @DisplayName("Lanza excepción cuando teléfono no es numérico o formato inválido")
    void shouldThrowWhenTelefonoIsInvalid(String telefono) {
        restauranteValido.setTelefono(telefono);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateRestaurante(restauranteValido));

        assertEquals("El teléfono debe ser numérico y puede incluir el símbolo + al inicio", ex.getMessage());
    }

    @Test
    @DisplayName("Lanza excepción cuando teléfono excede 13 caracteres")
    void shouldThrowWhenTelefonoExceeds13Characters() {
        restauranteValido.setTelefono("+57300123456789");

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateRestaurante(restauranteValido));

        assertEquals("El teléfono debe tener máximo 13 caracteres", ex.getMessage());
    }

    @Test
    @DisplayName("No lanza cuando teléfono tiene + al inicio")
    void shouldNotThrowWhenTelefonoHasPlusSign() {
        restauranteValido.setTelefono("+573001234567");

        assertDoesNotThrow(() -> adapter.validateRestaurante(restauranteValido));
    }

    @Test
    @DisplayName("No lanza cuando teléfono sin +")
    void shouldNotThrowWhenTelefonoWithoutPlus() {
        restauranteValido.setTelefono("3001234567");

        assertDoesNotThrow(() -> adapter.validateRestaurante(restauranteValido));
    }
}
