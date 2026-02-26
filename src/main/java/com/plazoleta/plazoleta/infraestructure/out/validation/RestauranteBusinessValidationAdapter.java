package com.plazoleta.plazoleta.infraestructure.out.validation;

import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.spi.RestauranteBusinessValidationPort;

public class RestauranteBusinessValidationAdapter implements RestauranteBusinessValidationPort {

    @Override
    public void validateRestaurante(Restaurante restaurante) {
        if (restaurante.getNombre() == null || restaurante.getNombre().trim().isEmpty()) {
            throw new DominioException("El nombre del restaurante es obligatorio");
        }
        if (restaurante.getNombre().matches("\\d+")) {
            throw new DominioException("El nombre del restaurante no puede contener solo números");
        }

        if (restaurante.getNit() == null || !restaurante.getNit().matches("\\d+")) {
            throw new DominioException("El NIT debe ser numérico");
        }

        if (restaurante.getTelefono() == null || !restaurante.getTelefono().matches("^\\+?\\d+$")) {
            throw new DominioException("El teléfono debe ser numérico y puede incluir el símbolo + al inicio");
        }
        if (restaurante.getTelefono().length() > 13) {
            throw new DominioException("El teléfono debe tener máximo 13 caracteres");
        }
    }
}