package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.api.PlatoServicePort;
import com.plazoleta.plazoleta.domain.exception.RestauranteNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.RestauranteNoPerteneceException;
import com.plazoleta.plazoleta.domain.exception.RolNoAutorizadoException;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.spi.PlatoPersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestauranteValidationPort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;
import com.plazoleta.plazoleta.domain.spi.PlatoBusinessValidationPort;

public class CrearPlatoUseCase implements PlatoServicePort {

    private final PlatoPersistencePort platoPersistencePort;
    private final UsuarioValidationPort usuarioValidationPort;
    private final RestauranteValidationPort restauranteValidationPort;
    private final PlatoBusinessValidationPort platoBusinessValidationPort;

    public CrearPlatoUseCase(PlatoPersistencePort platoPersistencePort,
                             UsuarioValidationPort usuarioValidationPort,
                             RestauranteValidationPort restauranteValidationPort,
                             PlatoBusinessValidationPort platoBusinessValidationPort) {
        this.platoPersistencePort = platoPersistencePort;
        this.usuarioValidationPort = usuarioValidationPort;
        this.restauranteValidationPort = restauranteValidationPort;
        this.platoBusinessValidationPort = platoBusinessValidationPort;
    }

    @Override
    public void crearPlato(Plato plato, Long propietarioId) {

        platoBusinessValidationPort.validateCreate(plato);

        var user = usuarioValidationPort.getUserById(propietarioId);
        if (!isOwnerRole(user.getRole())) {
            throw new RolNoAutorizadoException("El usuario no tiene el rol de propietario");
        }

        if (!restauranteValidationPort.restauranteExiste(plato.getRestauranteId())) {
            throw new RestauranteNoEncontradoException("El restaurante especificado no existe");
        }

        if (!restauranteValidationPort.restaurantePerteneceAPropietario(
                plato.getRestauranteId(), propietarioId)) {
            throw new RestauranteNoPerteneceException("El restaurante no pertenece al propietario");
        }

        platoPersistencePort.save(plato);
    }
    private boolean isOwnerRole(String role) {
        return "PROPIETARIO".equalsIgnoreCase(role) || "2".equals(role);
    }
}