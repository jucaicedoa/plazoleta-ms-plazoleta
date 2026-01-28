package com.plazoleta.plazoleta.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.infraestructure.out.jpa.adapter.RestauranteJpaAdapter;
import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.RestauranteEntity;
import com.plazoleta.plazoleta.infraestructure.out.jpa.mapper.RestauranteEntityMapper;
import com.plazoleta.plazoleta.infraestructure.out.jpa.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - RestaurantJpaAdapter")
class RestauranteJpaAdapterTest {

    @Mock
    private RestauranteRepository repository;

    @Mock
    private RestauranteEntityMapper mapper;

    private RestauranteJpaAdapter adapter;

    private Restaurante validRestaurant;
    private RestauranteEntity validEntity;

    @BeforeEach
    void setUp() {
        adapter = new RestauranteJpaAdapter(repository, mapper);

        validRestaurant = new Restaurante();
        validRestaurant.setNombre("La Arepa Feliz");
        validRestaurant.setDireccion("Calle 123 #45-67");
        validRestaurant.setPropietarioId(1L);
        validRestaurant.setTelefono("+573001234567");
        validRestaurant.setUrlLogo("https://ejemplo.com/logo.png");
        validRestaurant.setNit("900123456");

        validEntity = new RestauranteEntity();
        validEntity.setNombre("La Arepa Feliz");
        validEntity.setDireccion("Calle 123 #45-67");
        validEntity.setPropietarioId(1L);
        validEntity.setTelefono("+573001234567");
        validEntity.setUrlLogo("https://ejemplo.com/logo.png");
        validEntity.setNit("900123456");
    }

    @Test
    @DisplayName("Debe delegar correctamente al mapper y al repository")
    void shouldDelegateToMapperAndRepository() {
        // Arrange
        when(mapper.toEntity(validRestaurant)).thenReturn(validEntity);

        // Act
        adapter.save(validRestaurant);

        // Assert
        verify(mapper, times(1)).toEntity(validRestaurant);
        verify(repository, times(1)).save(validEntity);
    }

    @Test
    @DisplayName("Debe pasar el modelo de dominio correcto al mapper")
    void shouldPassCorrectDomainModelToMapper() {
        // Arrange
        when(mapper.toEntity(any(Restaurante.class))).thenReturn(validEntity);

        // Act
        adapter.save(validRestaurant);

        // Assert
        verify(mapper).toEntity(validRestaurant);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    @DisplayName("Debe pasar la entidad correcta al repository")
    void shouldPassCorrectEntityToRepository() {
        // Arrange
        when(mapper.toEntity(validRestaurant)).thenReturn(validEntity);

        // Act
        adapter.save(validRestaurant);

        // Assert
        verify(repository).save(validEntity);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("No debe llamar al repository si el mapper falla")
    void shouldNotCallRepositoryIfMapperFails() {
        // Arrange
        when(mapper.toEntity(validRestaurant)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        try {
            adapter.save(validRestaurant);
        } catch (RuntimeException e) {
            // Esperado
        }

        verify(mapper, times(1)).toEntity(validRestaurant);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debe manejar múltiples llamadas independientes")
    void shouldHandleMultipleIndependentCalls() {
        // Arrange
        when(mapper.toEntity(any(Restaurante.class))).thenReturn(validEntity);

        Restaurante restaurant2 = new Restaurante();
        restaurant2.setNombre("El Buen Sabor");
        restaurant2.setNit("800987654");

        // Act
        adapter.save(validRestaurant);
        adapter.save(restaurant2);

        // Assert
        verify(mapper, times(2)).toEntity(any(Restaurante.class));
        verify(repository, times(2)).save(any(RestauranteEntity.class));
    }
}