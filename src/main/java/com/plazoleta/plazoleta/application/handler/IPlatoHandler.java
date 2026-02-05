package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.ActualizarPlatoRequestDto;
import com.plazoleta.plazoleta.application.dto.CrearPlatoRequestDto;

public interface IPlatoHandler {

    void createDish(CrearPlatoRequestDto dto);

    void updateDish(Long platoId, ActualizarPlatoRequestDto dto);
}