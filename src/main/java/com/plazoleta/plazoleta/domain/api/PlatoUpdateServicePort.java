package com.plazoleta.plazoleta.domain.api;

public interface PlatoUpdateServicePort {

    void updateDish(Long platoId, Integer precio, String descripcion, Long propietarioId);
}