package com.plazoleta.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurante {

    private Long id;
    private String nombre;
    private String direccion;
    private Long propietarioId;
    private String telefono;
    private String urlLogo;
    private String nit;
}