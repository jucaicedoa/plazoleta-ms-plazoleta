package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.api.RestauranteServicePort;
import com.plazoleta.plazoleta.domain.exception.RolNoAutorizadoException;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.model.UsuarioModelo;
import com.plazoleta.plazoleta.domain.spi.RestaurantePersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestauranteBusinessValidationPort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;

public class CrearRestauranteUseCase implements RestauranteServicePort {

    private final RestaurantePersistencePort persistencePort;
    private final UsuarioValidationPort userValidationPort;
    private final RestauranteBusinessValidationPort restauranteBusinessValidationPort;

    public CrearRestauranteUseCase(RestaurantePersistencePort persistencePort,
                                   UsuarioValidationPort userValidationPort,
                                   RestauranteBusinessValidationPort restauranteBusinessValidationPort) {
        this.persistencePort = persistencePort;
        this.userValidationPort = userValidationPort;
        this.restauranteBusinessValidationPort = restauranteBusinessValidationPort;
    }

    @Override
    public void crearRestaurante(Restaurante restaurant) {
        restauranteBusinessValidationPort.validateRestaurante(restaurant);

        UsuarioModelo user = userValidationPort.getUserById(restaurant.getPropietarioId());

        if (!isOwnerRole(user.getRole())) {
            throw new RolNoAutorizadoException("El usuario no tiene rol de propietario");
        }

        persistencePort.save(restaurant);
    }

    private boolean isOwnerRole(String role) {
        return "PROPIETARIO".equalsIgnoreCase(role) || "2".equals(role);
    }
}