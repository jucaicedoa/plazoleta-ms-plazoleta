package com.plazoleta.plazoleta.domain.spi;

import com.plazoleta.plazoleta.domain.model.Plato;

public interface PlatoPersistencePort {
    Plato getById(Long platoId);
    void save(Plato plato);
}