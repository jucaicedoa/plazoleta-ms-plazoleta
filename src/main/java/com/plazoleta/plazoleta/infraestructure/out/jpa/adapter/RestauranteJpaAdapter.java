package com.plazoleta.plazoleta.infraestructure.out.jpa.adapter;

import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.spi.RestaurantePersistencePort;
import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.RestauranteEntity;
import com.plazoleta.plazoleta.infraestructure.out.jpa.mapper.RestauranteEntityMapper;
import com.plazoleta.plazoleta.infraestructure.out.jpa.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestauranteJpaAdapter implements RestaurantePersistencePort {

    private final RestauranteRepository repository;
    private final RestauranteEntityMapper mapper;

    @Override
    public void save(Restaurante restaurant) {
        RestauranteEntity entity = mapper.toEntity(restaurant);
        repository.save(entity);
    }
}