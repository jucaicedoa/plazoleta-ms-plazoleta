package com.plazoleta.plazoleta.infrastructure.out.validation;

import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.infraestructure.out.validation.PlatoBusinessValidationAdapter;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlatoBusinessValidationAdapter")
class PlatoBusinessValidationAdapterTest {

    private PlatoBusinessValidationAdapter adapter;

    private Plato platoValido;

    @BeforeEach
    void setUp() {
        adapter = new PlatoBusinessValidationAdapter();
        platoValido = new Plato(
                "Hamburguesa",
                25000,
                "Descripción",
                "http://img.com/burger.png",
                "COMIDA_RAPIDA",
                10L
        );
    }

    // --- validateCreate ---

    @Test
    @DisplayName("validateCreate: no lanza cuando plato es válido")
    void validateCreate_shouldNotThrowWhenPlatoIsValid() {
        assertDoesNotThrow(() -> adapter.validateCreate(platoValido));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("validateCreate: lanza cuando nombre es null o vacío")
    void validateCreate_shouldThrowWhenNombreIsNullOrEmpty(String nombre) {
        platoValido.setNombre(nombre);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateCreate(platoValido));

        assertEquals("El nombre del plato es obligatorio", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("validateCreate: lanza cuando descripción es null o vacía")
    void validateCreate_shouldThrowWhenDescripcionIsNullOrEmpty(String desc) {
        platoValido.setDescripcion(desc);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateCreate(platoValido));

        assertEquals("La descripción del plato es obligatoria", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("validateCreate: lanza cuando urlImagen es null o vacía")
    void validateCreate_shouldThrowWhenUrlImagenIsNullOrEmpty(String url) {
        platoValido.setUrlImagen(url);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateCreate(platoValido));

        assertEquals("La URL de la imagen es obligatoria", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("validateCreate: lanza cuando categoría es null o vacía")
    void validateCreate_shouldThrowWhenCategoriaIsNullOrEmpty(String cat) {
        platoValido.setCategoria(cat);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateCreate(platoValido));

        assertEquals("La categoría del plato es obligatoria", ex.getMessage());
    }

    @Test
    @DisplayName("validateCreate: lanza cuando restauranteId es null")
    void validateCreate_shouldThrowWhenRestauranteIdIsNull() {
        platoValido.setRestauranteId(null);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateCreate(platoValido));

        assertEquals("El ID del restaurante es obligatorio", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("validateCreate: lanza cuando precio es <= 0")
    void validateCreate_shouldThrowWhenPrecioIsZeroOrNegative(int precio) {
        platoValido.setPrecio(precio);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateCreate(platoValido));

        assertEquals("El precio debe ser mayor a cero", ex.getMessage());
    }

    @Test
    @DisplayName("validateCreate: lanza cuando precio es null")
    void validateCreate_shouldThrowWhenPrecioIsNull() {
        platoValido.setPrecio(null);

        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateCreate(platoValido));

        assertEquals("El precio debe ser mayor a cero", ex.getMessage());
    }

    @Test
    @DisplayName("validateCreate: establece activo true cuando es null")
    void validateCreate_shouldSetActivoTrueWhenNull() {
        platoValido.setActivo(null);

        adapter.validateCreate(platoValido);

        assertTrue(platoValido.getActivo());
    }

    @Test
    @DisplayName("validateCreate: no modifica activo cuando ya tiene valor")
    void validateCreate_shouldNotChangeActivoWhenAlreadySet() {
        platoValido.setActivo(false);

        adapter.validateCreate(platoValido);

        assertEquals(false, platoValido.getActivo());
    }

    // --- validateUpdate ---

    @Test
    @DisplayName("validateUpdate: no lanza cuando precio y descripción son válidos")
    void validateUpdate_shouldNotThrowWhenValid() {
        assertDoesNotThrow(() -> adapter.validateUpdate(18000, "Nueva descripción"));
    }

    @Test
    @DisplayName("validateUpdate: lanza cuando precio es null")
    void validateUpdate_shouldThrowWhenPrecioIsNull() {
        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateUpdate(null, "Desc"));

        assertEquals("El precio debe ser mayor a cero", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("validateUpdate: lanza cuando precio es <= 0")
    void validateUpdate_shouldThrowWhenPrecioIsZeroOrNegative(int precio) {
        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateUpdate(precio, "Desc"));

        assertEquals("El precio debe ser mayor a cero", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("validateUpdate: lanza cuando descripción es null o vacía")
    void validateUpdate_shouldThrowWhenDescripcionIsNullOrEmpty(String desc) {
        DominioException ex = assertThrows(DominioException.class,
                () -> adapter.validateUpdate(18000, desc));

        assertEquals("La descripción del plato es obligatoria", ex.getMessage());
    }
}