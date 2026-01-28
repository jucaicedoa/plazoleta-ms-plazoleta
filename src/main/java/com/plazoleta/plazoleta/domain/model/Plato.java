package com.plazoleta.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Plato {

    private Long id;
    private String nombre;
    private Integer precio;
    private String descripcion;
    private String urlImagen;
    private String categoria;
    private Long restauranteId;
    private Boolean activo;

    public Plato(String nombre, Integer precio, String descripcion,
                String urlImagen, String categoria, Long restauranteId) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.restauranteId = restauranteId;
        this.activo = true;
    }
}