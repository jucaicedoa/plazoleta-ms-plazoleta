package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.api.RestauranteServicePort;
import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.exception.RolNoAutorizadoException;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.model.UsuarioModelo;
import com.plazoleta.plazoleta.domain.spi.RestaurantePersistencePort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;

public class CrearRestauranteUseCase implements RestauranteServicePort {

    private final RestaurantePersistencePort persistencePort;
    private final UsuarioValidationPort userValidationPort;

    public CrearRestauranteUseCase(RestaurantePersistencePort persistencePort,
                                   UsuarioValidationPort userValidationPort) {
        this.persistencePort = persistencePort;
        this.userValidationPort = userValidationPort;
    }

    @Override
    public void crearRestaurante(Restaurante restaurant) {
        if (restaurant.getNombre() == null || restaurant.getNombre().trim().isEmpty()) {
            throw new DominioException("El nombre del restaurante es obligatorio");
        }
        if (restaurant.getNombre().matches("\\d+")) {
            throw new DominioException("El nombre del restaurante no puede contener solo números");
        }

        if (restaurant.getNit() == null || !restaurant.getNit().matches("\\d+")) {
            throw new DominioException("El NIT debe ser numérico");
        }

        if (restaurant.getTelefono() == null || !restaurant.getTelefono().matches("^\\+?\\d+$")) {
            throw new DominioException("El teléfono debe ser numérico y puede incluir el símbolo + al inicio");
        }
        if (restaurant.getTelefono().length() > 13) {
            throw new DominioException("El teléfono debe tener máximo 13 caracteres");
        }

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