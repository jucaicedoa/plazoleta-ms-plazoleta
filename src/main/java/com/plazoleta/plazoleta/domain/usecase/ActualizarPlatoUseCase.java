package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.api.PlatoUpdateServicePort;
import com.plazoleta.plazoleta.domain.exception.PlatoNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.RestauranteNoPerteneceException;
import com.plazoleta.plazoleta.domain.exception.RolNoAutorizadoException;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.spi.PlatoPersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestauranteValidationPort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;
import com.plazoleta.plazoleta.domain.spi.PlatoBusinessValidationPort;

public class ActualizarPlatoUseCase implements PlatoUpdateServicePort {

    private final PlatoPersistencePort platoPersistencePort;
    private final UsuarioValidationPort usuarioValidationPort;
    private final RestauranteValidationPort restauranteValidationPort;
    private final PlatoBusinessValidationPort platoBusinessValidationPort;

    public ActualizarPlatoUseCase(PlatoPersistencePort platoPersistencePort,
                             UsuarioValidationPort usuarioValidationPort,
                             RestauranteValidationPort restauranteValidationPort,
                             PlatoBusinessValidationPort platoBusinessValidationPort) {
        this.platoPersistencePort = platoPersistencePort;
        this.usuarioValidationPort = usuarioValidationPort;
        this.restauranteValidationPort = restauranteValidationPort;
        this.platoBusinessValidationPort = platoBusinessValidationPort;
    }

    @Override
    public void updateDish(Long platoId, Integer precio, String descripcion, Long propietarioId) {

        var user = usuarioValidationPort.getUserById(propietarioId);
        if (!isOwnerRole(user.getRole())) {
            throw new RolNoAutorizadoException("El usuario no tiene el rol de propietario");
        }

        Plato plato = platoPersistencePort.getById(platoId);
        if (plato == null) {
            throw new PlatoNoEncontradoException("El plato no existe");
        }

        if (!restauranteValidationPort.restaurantePerteneceAPropietario(plato.getRestauranteId(), propietarioId)) {
            throw new RestauranteNoPerteneceException("El restaurante no pertenece al propietario");
        }

        platoBusinessValidationPort.validateUpdate(precio, descripcion);
        plato.setPrecio(precio);
        plato.setDescripcion(descripcion);

        platoPersistencePort.save(plato);
    }

    private boolean isOwnerRole(String role) {
        return "PROPIETARIO".equalsIgnoreCase(role) || "2".equals(role);
    }
}