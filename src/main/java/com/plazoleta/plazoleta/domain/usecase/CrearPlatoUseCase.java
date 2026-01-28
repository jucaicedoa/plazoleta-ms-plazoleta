package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.api.PlatoServicePort;
import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.spi.PlatoPersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestauranteValidationPort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;

public class CrearPlatoUseCase implements PlatoServicePort {

    private final PlatoPersistencePort platoPersistencePort;
    private final UsuarioValidationPort usuarioValidationPort;
    private final RestauranteValidationPort restauranteValidationPort;

    public CrearPlatoUseCase(PlatoPersistencePort platoPersistencePort,
                             UsuarioValidationPort usuarioValidationPort,
                             RestauranteValidationPort restauranteValidationPort) {
        this.platoPersistencePort = platoPersistencePort;
        this.usuarioValidationPort = usuarioValidationPort;
        this.restauranteValidationPort = restauranteValidationPort;
    }

    @Override
    public void crearPlato(Plato plato, Long propietarioId) {

        validarCamposRequeridos(plato);

        var user = usuarioValidationPort.getUserById(propietarioId);
        if (user == null) {
            throw new DominioException("El usuario no existe");
        }
        if (!isOwnerRole(user.getRole())) {
            throw new DominioException("El usuario no tiene el rol de propietario");
        }

        if (!restauranteValidationPort.restauranteExiste(plato.getRestauranteId())) {
            throw new DominioException("El restaurante especificado no existe");
        }

        if (!restauranteValidationPort.restaurantePerteneceAPropietario(
                plato.getRestauranteId(), propietarioId)) {
            throw new DominioException("El restaurante no pertenece al propietario");
        }

        if (plato.getPrecio() == null || plato.getPrecio() <= 0) {
            throw new DominioException("El precio debe ser mayor a cero");
        }

        if (plato.getActivo() == null) {
            plato.setActivo(true);
        }

        platoPersistencePort.save(plato);
    }

    private void validarCamposRequeridos(Plato plato) {
        if (plato.getNombre() == null || plato.getNombre().trim().isEmpty()) {
            throw new DominioException("El nombre del plato es obligatorio");
        }
        if (plato.getDescripcion() == null || plato.getDescripcion().trim().isEmpty()) {
            throw new DominioException("La descripción del plato es obligatoria");
        }
        if (plato.getUrlImagen() == null || plato.getUrlImagen().trim().isEmpty()) {
            throw new DominioException("La URL de la imagen es obligatoria");
        }
        if (plato.getCategoria() == null || plato.getCategoria().trim().isEmpty()) {
            throw new DominioException("La categoría del plato es obligatoria");
        }
        if (plato.getRestauranteId() == null) {
            throw new DominioException("El ID del restaurante es obligatorio");
        }
    }
    private boolean isOwnerRole(String role) {
        return "PROPIETARIO".equalsIgnoreCase(role) || "2".equals(role);
    }
}