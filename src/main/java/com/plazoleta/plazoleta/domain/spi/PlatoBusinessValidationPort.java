package com.plazoleta.plazoleta.domain.spi;

import com.plazoleta.plazoleta.domain.model.Plato;

public interface PlatoBusinessValidationPort {

    void validateCreate(Plato plato);

    void validateUpdate(Integer precio, String descripcion);
}