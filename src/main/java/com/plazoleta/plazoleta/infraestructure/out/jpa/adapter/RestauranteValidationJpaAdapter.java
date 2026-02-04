package com.plazoleta.plazoleta.infraestructure.out.jpa.adapter;

import com.plazoleta.plazoleta.domain.spi.RestauranteValidationPort;
import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.RestauranteEntity;
import com.plazoleta.plazoleta.infraestructure.out.jpa.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@RequiredArgsConstructor
public class RestauranteValidationJpaAdapter implements RestauranteValidationPort {

    private final RestauranteRepository restauranteRepository;

    @Override
    public boolean restaurantePerteneceAPropietario(Long restauranteId, Long propietarioId) {
        Optional<RestauranteEntity> restaurant = restauranteRepository.findById(restauranteId);
        return restaurant.isPresent() &&
                restaurant.get().getPropietarioId().equals(propietarioId);
    }

    @Override
    public boolean restauranteExiste(Long restauranteId) {
        return restauranteRepository.existsById(restauranteId);
    }
}