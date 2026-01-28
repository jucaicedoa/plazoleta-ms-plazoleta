package com.plazoleta.plazoleta.domain.spi;

import com.plazoleta.plazoleta.domain.model.Restaurante;

public interface RestaurantePersistencePort {
    void save(Restaurante restaurante);
}