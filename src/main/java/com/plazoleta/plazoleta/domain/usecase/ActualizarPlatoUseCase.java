package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.api.PlatoUpdateServicePort;
import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.spi.PlatoPersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestauranteValidationPort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;

public class ActualizarPlatoUseCase implements PlatoUpdateServicePort {

    private final PlatoPersistencePort platoPersistencePort;
    private final UsuarioValidationPort usuarioValidationPort;
    private final RestauranteValidationPort restauranteValidationPort;

    public ActualizarPlatoUseCase(PlatoPersistencePort platoPersistencePort,
                             UsuarioValidationPort usuarioValidationPort,
                             RestauranteValidationPort restauranteValidationPort) {
        this.platoPersistencePort = platoPersistencePort;
        this.usuarioValidationPort = usuarioValidationPort;
        this.restauranteValidationPort = restauranteValidationPort;
    }

    @Override
    public void updateDish(Long platoId, Integer precio, String descripcion, Long propietarioId) {

        var user = usuarioValidationPort.getUserById(propietarioId);
        if (user == null) {
            throw new DominioException("El usuario no existe");
        }
        if (!isOwnerRole(user.getRole())) {
            throw new DominioException("El usuario no tiene el rol de propietario");
        }

        Plato plato = platoPersistencePort.getById(platoId);
        if (plato == null) {
            throw new DominioException("El plato no existe");
        }

        if (!restauranteValidationPort.restaurantePerteneceAPropietario(plato.getRestauranteId(), propietarioId)) {
            throw new DominioException("El restaurante no pertenece al propietario");
        }

        if (precio == null || precio <= 0) {
            throw new DominioException("El precio debe ser mayor a cero");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new DominioException("La descripción del plato es obligatoria");
        }
        plato.setPrecio(precio);
        plato.setDescripcion(descripcion);

        platoPersistencePort.save(plato);
    }

    private boolean isOwnerRole(String role) {
        return "PROPIETARIO".equalsIgnoreCase(role) || "2".equals(role);
    }
}