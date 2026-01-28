package com.plazoleta.plazoleta.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPlatoRequestDto {
    private Integer precio;
    private String descripcion;
}