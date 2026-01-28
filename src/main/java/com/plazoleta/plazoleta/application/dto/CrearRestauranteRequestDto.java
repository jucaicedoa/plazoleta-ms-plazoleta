package com.plazoleta.plazoleta.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearRestauranteRequestDto {

    private String nombre;
    private String direccion;
    private Long propietarioId;
    private String telefono;
    private String urlLogo;
    private String nit;
}