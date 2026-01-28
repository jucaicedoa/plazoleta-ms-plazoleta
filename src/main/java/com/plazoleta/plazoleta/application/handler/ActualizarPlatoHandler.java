package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.ActualizarPlatoRequestDto;
import com.plazoleta.plazoleta.domain.api.PlatoUpdateServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActualizarPlatoHandler {

    private final PlatoUpdateServicePort platoUpdateServicePort;

    public void handle(Long platoId, ActualizarPlatoRequestDto dto, Long propietarioId) {
        platoUpdateServicePort.updateDish(platoId, dto.getPrecio(), dto.getDescripcion(), propietarioId);
    }
}