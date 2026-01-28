package com.plazoleta.plazoleta.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.infraestructure.out.jpa.adapter.PlatoJpaAdapter;
import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.PlatoEntity;
import com.plazoleta.plazoleta.infraestructure.out.jpa.mapper.PlatoEntityMapper;
import com.plazoleta.plazoleta.infraestructure.out.jpa.repository.PlatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PlatoJpaAdapterTest {

    @Mock
    private PlatoRepository dishRepository;

    @Mock
    private PlatoEntityMapper dishEntityMapper;

    @InjectMocks
    private PlatoJpaAdapter dishJpaAdapter;

    private Plato validDish;
    private PlatoEntity dishEntity;

    @BeforeEach
    void setUp() {
        validDish = new Plato(
                "Hamburguesa Especial",
                25000,
                "Carne artesanal con queso cheddar",
                "http://img.com/burger.png",
                "COMIDA_RAPIDA",
                10L
        );

        dishEntity = new PlatoEntity();
        dishEntity.setId(1L);
        dishEntity.setNombre("Hamburguesa Especial");
        dishEntity.setPrecio(25000);
        dishEntity.setDescripcion("Carne artesanal con queso cheddar");
        dishEntity.setUrlImagen("http://img.com/burger.png");
        dishEntity.setCategoria("COMIDA_RAPIDA");
        dishEntity.setRestauranteId(10L);
        dishEntity.setActivo(true);
    }

    @Test
    @DisplayName("Debería guardar un plato exitosamente")
    void shouldSaveDishSuccessfully() {
        // Given
        when(dishEntityMapper.toEntity(validDish)).thenReturn(dishEntity);
        when(dishRepository.save(dishEntity)).thenReturn(dishEntity);

        // When
        dishJpaAdapter.save(validDish);

        // Then
        verify(dishEntityMapper).toEntity(validDish);
        verify(dishRepository).save(dishEntity);
    }

    @Test
    @DisplayName("Debería obtener un plato por ID cuando existe")
    void shouldGetDishByIdWhenExists() {
        // Given
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dishEntity));
        when(dishEntityMapper.toDomain(dishEntity)).thenReturn(validDish);

        // When
        Plato result = dishJpaAdapter.getById(1L);

        // Then
        assertNotNull(result);
        verify(dishRepository).findById(1L);
        verify(dishEntityMapper).toDomain(dishEntity);
    }

    @Test
    @DisplayName("Debería retornar null al obtener un plato por ID cuando no existe")
    void shouldReturnNullWhenDishNotFoundById() {
        // Given
        when(dishRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Plato result = dishJpaAdapter.getById(999L);

        // Then
        assertNull(result);
        verify(dishRepository).findById(999L);
    }

    @Test
    @DisplayName("Debería mapear correctamente de dominio a entidad")
    void shouldMapDomainToEntity() {
        // Given
        when(dishEntityMapper.toEntity(validDish)).thenReturn(dishEntity);
        when(dishRepository.save(any(PlatoEntity.class))).thenReturn(dishEntity);

        // When
        dishJpaAdapter.save(validDish);

        // Then
        ArgumentCaptor<Plato> dishCaptor = ArgumentCaptor.forClass(Plato.class);
        verify(dishEntityMapper).toEntity(dishCaptor.capture());

        Plato capturedDish = dishCaptor.getValue();
        assertEquals("Hamburguesa Especial", capturedDish.getNombre());
        assertEquals(25000, capturedDish.getPrecio());
        assertEquals("COMIDA_RAPIDA", capturedDish.getCategoria());
    }

    @Test
    @DisplayName("Debería pasar la entidad correcta al repositorio")
    void shouldPassCorrectEntityToRepository() {
        // Given
        when(dishEntityMapper.toEntity(validDish)).thenReturn(dishEntity);
        when(dishRepository.save(any(PlatoEntity.class))).thenReturn(dishEntity);

        // When
        dishJpaAdapter.save(validDish);

        // Then
        ArgumentCaptor<PlatoEntity> entityCaptor = ArgumentCaptor.forClass(PlatoEntity.class);
        verify(dishRepository).save(entityCaptor.capture());

        PlatoEntity capturedEntity = entityCaptor.getValue();
        assertNotNull(capturedEntity);
        assertEquals("Hamburguesa Especial", capturedEntity.getNombre());
        assertEquals(25000, capturedEntity.getPrecio());
        assertTrue(capturedEntity.getActivo());
    }

    @Test
    @DisplayName("Debería guardar plato con activo = true")
    void shouldSaveDishWithActiveTrue() {
        // Given
        validDish.setActivo(true);
        dishEntity.setActivo(true);

        when(dishEntityMapper.toEntity(validDish)).thenReturn(dishEntity);
        when(dishRepository.save(dishEntity)).thenReturn(dishEntity);

        // When
        dishJpaAdapter.save(validDish);

        // Then
        ArgumentCaptor<PlatoEntity> entityCaptor = ArgumentCaptor.forClass(PlatoEntity.class);
        verify(dishRepository).save(entityCaptor.capture());

        assertTrue(entityCaptor.getValue().getActivo());
    }

    @Test
    @DisplayName("Debería guardar plato con diferentes categorías")
    void shouldSaveDishWithDifferentCategories() {
        // Given
        validDish.setCategoria("PIZZA");
        dishEntity.setCategoria("PIZZA");

        when(dishEntityMapper.toEntity(validDish)).thenReturn(dishEntity);
        when(dishRepository.save(dishEntity)).thenReturn(dishEntity);

        // When
        dishJpaAdapter.save(validDish);

        // Then
        verify(dishRepository).save(any(PlatoEntity.class));
    }

    @Test
    @DisplayName("Debería guardar plato asociado al restaurante correcto")
    void shouldSaveDishWithCorrectRestaurant() {
        // Given
        Long restaurantId = 42L;
        validDish.setRestauranteId(restaurantId);
        dishEntity.setRestauranteId(restaurantId);

        when(dishEntityMapper.toEntity(validDish)).thenReturn(dishEntity);
        when(dishRepository.save(dishEntity)).thenReturn(dishEntity);

        // When
        dishJpaAdapter.save(validDish);

        // Then
        ArgumentCaptor<PlatoEntity> entityCaptor = ArgumentCaptor.forClass(PlatoEntity.class);
        verify(dishRepository).save(entityCaptor.capture());

        assertEquals(restaurantId, entityCaptor.getValue().getRestauranteId());
    }

    @Test
    @DisplayName("Debería guardar plato con precio alto")
    void shouldSaveDishWithHighPrice() {
        // Given
        Integer highPrice = 150000;
        validDish.setPrecio(highPrice);
        dishEntity.setPrecio(highPrice);

        when(dishEntityMapper.toEntity(validDish)).thenReturn(dishEntity);
        when(dishRepository.save(dishEntity)).thenReturn(dishEntity);

        // When
        dishJpaAdapter.save(validDish);

        // Then
        ArgumentCaptor<PlatoEntity> entityCaptor = ArgumentCaptor.forClass(PlatoEntity.class);
        verify(dishRepository).save(entityCaptor.capture());

        assertEquals(highPrice, entityCaptor.getValue().getPrecio());
    }

    @Test
    @DisplayName("Debería guardar plato con URL de imagen larga")
    void shouldSaveDishWithLongImageUrl() {
        // Given
        String longUrl = "http://example.com/very/long/path/to/image/burger-special-deluxe.png";
        validDish.setUrlImagen(longUrl);
        dishEntity.setUrlImagen(longUrl);

        when(dishEntityMapper.toEntity(validDish)).thenReturn(dishEntity);
        when(dishRepository.save(dishEntity)).thenReturn(dishEntity);

        // When
        dishJpaAdapter.save(validDish);

        // Then
        ArgumentCaptor<PlatoEntity> entityCaptor = ArgumentCaptor.forClass(PlatoEntity.class);
        verify(dishRepository).save(entityCaptor.capture());

        assertEquals(longUrl, entityCaptor.getValue().getUrlImagen());
    }

    @Test
    @DisplayName("Debería invocar mapper antes que repository")
    void shouldInvokeMapperBeforeRepository() {
        // Given
        when(dishEntityMapper.toEntity(validDish)).thenReturn(dishEntity);
        when(dishRepository.save(dishEntity)).thenReturn(dishEntity);

        // When
        dishJpaAdapter.save(validDish);

        // Then
        // Verificar orden de invocación
        var orderVerifier = inOrder(dishEntityMapper, dishRepository);
        orderVerifier.verify(dishEntityMapper).toEntity(validDish);
        orderVerifier.verify(dishRepository).save(dishEntity);
    }
}