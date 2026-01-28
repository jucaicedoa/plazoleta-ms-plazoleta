package com.plazoleta.plazoleta.domain.api;

import com.plazoleta.plazoleta.domain.model.Plato;

public interface PlatoServicePort {

    void crearPlato(Plato plato, Long propietarioId);
}