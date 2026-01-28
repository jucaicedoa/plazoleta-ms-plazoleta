package com.plazoleta.plazoleta.domain.spi;

public interface RestauranteValidationPort {
    boolean restaurantePerteneceAPropietario(Long restauranteId, Long propietarioId);
    boolean restauranteExiste(Long restauranteId);
}