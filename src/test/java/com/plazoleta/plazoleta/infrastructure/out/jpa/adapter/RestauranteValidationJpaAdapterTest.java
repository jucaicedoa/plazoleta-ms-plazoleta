package com.plazoleta.plazoleta.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta.infraestructure.out.jpa.adapter.RestauranteValidationJpaAdapter;
import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.RestauranteEntity;
import com.plazoleta.plazoleta.infraestructure.out.jpa.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestauranteValidationJpaAdapterTest {

    @Mock
    private RestauranteRepository restaurantRepository;

    @InjectMocks
    private RestauranteValidationJpaAdapter restaurantValidationJpaAdapter;

    private RestauranteEntity restaurantEntity;
    private Long restaurantId;
    private Long propietarioId;

    @BeforeEach
    void setUp() {
        restaurantId = 10L;
        propietarioId = 1L;

        restaurantEntity = new RestauranteEntity();
        restaurantEntity.setId(restaurantId);
        restaurantEntity.setNombre("Restaurante Test");
        restaurantEntity.setDireccion("Calle 123");
        restaurantEntity.setPropietarioId(propietarioId);
        restaurantEntity.setTelefono("+573001234567");
        restaurantEntity.setUrlLogo("http://logo.com/test.png");
        restaurantEntity.setNit("900123456");
    }

    @Test
    @DisplayName("Debería retornar true cuando el restaurante pertenece al propietario")
    void shouldReturnTrueWhenRestaurantBelongsToOwner() {
        // Given
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurantEntity));

        // When
        boolean result = restaurantValidationJpaAdapter.restaurantePerteneceAPropietario(restaurantId, propietarioId);

        // Then
        assertTrue(result);
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    @DisplayName("Debería retornar false cuando el restaurante no pertenece al propietario")
    void shouldReturnFalseWhenRestaurantDoesNotBelongToOwner() {
        // Given
        Long differentPropietarioId = 999L;
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurantEntity));

        // When
        boolean result = restaurantValidationJpaAdapter.restaurantePerteneceAPropietario(restaurantId, differentPropietarioId);

        // Then
        assertFalse(result);
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    @DisplayName("Debería retornar false cuando el restaurante no existe")
    void shouldReturnFalseWhenRestaurantDoesNotExist() {
        // Given
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // When
        boolean result = restaurantValidationJpaAdapter.restaurantePerteneceAPropietario(restaurantId, propietarioId);

        // Then
        assertFalse(result);
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    @DisplayName("Debería retornar true cuando el restaurante existe")
    void shouldReturnTrueWhenRestaurantExists() {
        // Given
        when(restaurantRepository.existsById(restaurantId)).thenReturn(true);

        // When
        boolean result = restaurantValidationJpaAdapter.restauranteExiste(restaurantId);

        // Then
        assertTrue(result);
        verify(restaurantRepository).existsById(restaurantId);
    }

    @Test
    @DisplayName("Debería retornar false cuando el restaurante no existe")
    void shouldReturnFalseWhenRestaurantDoesNotExistInExistsMethod() {
        // Given
        when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

        // When
        boolean result = restaurantValidationJpaAdapter.restauranteExiste(restaurantId);

        // Then
        assertFalse(result);
        verify(restaurantRepository).existsById(restaurantId);
    }

    @Test
    @DisplayName("Debería validar correctamente con diferentes IDs de restaurante")
    void shouldValidateWithDifferentRestaurantIds() {
        // Given
        Long anotherRestaurantId = 42L;
        RestauranteEntity anotherRestaurant = new RestauranteEntity();
        anotherRestaurant.setId(anotherRestaurantId);
        anotherRestaurant.setPropietarioId(propietarioId);

        when(restaurantRepository.findById(anotherRestaurantId)).thenReturn(Optional.of(anotherRestaurant));

        // When
        boolean result = restaurantValidationJpaAdapter.restaurantePerteneceAPropietario(anotherRestaurantId, propietarioId);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Debería validar correctamente con diferentes IDs de propietario")
    void shouldValidateWithDifferentPropietarioIds() {
        // Given
        Long anotherPropietarioId = 42L;
        restaurantEntity.setPropietarioId(anotherPropietarioId);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurantEntity));

        // When
        boolean resultCorrect = restaurantValidationJpaAdapter.restaurantePerteneceAPropietario(restaurantId, anotherPropietarioId);
        boolean resultIncorrect = restaurantValidationJpaAdapter.restaurantePerteneceAPropietario(restaurantId, propietarioId);

        // Then
        assertTrue(resultCorrect);
        assertFalse(resultIncorrect);
    }

    @Test
    @DisplayName("Debería manejar IDs nulos correctamente en belongsToOwner")
    void shouldHandleNullIdsInBelongsToOwner() {
        // Given
        when(restaurantRepository.findById(null)).thenReturn(Optional.empty());

        // When
        boolean result = restaurantValidationJpaAdapter.restaurantePerteneceAPropietario(null, propietarioId);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Debería manejar IDs nulos correctamente en exists")
    void shouldHandleNullIdsInExists() {
        // Given
        when(restaurantRepository.existsById(null)).thenReturn(false);

        // When
        boolean result = restaurantValidationJpaAdapter.restauranteExiste(null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Debería comparar IDs correctamente usando equals")
    void shouldCompareIdsUsingEquals() {
        // Given
        Long propietarioIdAsLong = Long.valueOf(1);
        restaurantEntity.setPropietarioId(propietarioIdAsLong);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurantEntity));

        // When
        boolean result = restaurantValidationJpaAdapter.restaurantePerteneceAPropietario(restaurantId, 1L);

        // Then
        assertTrue(result);
    }
}