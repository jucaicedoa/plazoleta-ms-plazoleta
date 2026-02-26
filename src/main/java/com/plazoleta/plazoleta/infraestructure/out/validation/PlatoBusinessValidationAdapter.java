package com.plazoleta.plazoleta.infraestructure.out.validation;

import com.plazoleta.plazoleta.domain.exception.DominioException;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.spi.PlatoBusinessValidationPort;

public class PlatoBusinessValidationAdapter implements PlatoBusinessValidationPort {

    @Override
    public void validateCreate(Plato plato) {
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

        validatePrecio(plato.getPrecio());

        if (plato.getActivo() == null) {
            plato.setActivo(true);
        }
    }

    @Override
    public void validateUpdate(Integer precio, String descripcion) {
        validatePrecio(precio);

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new DominioException("La descripción del plato es obligatoria");
        }
    }

    private void validatePrecio(Integer precio) {
        if (precio == null || precio <= 0) {
            throw new DominioException("El precio debe ser mayor a cero");
        }
    }
}