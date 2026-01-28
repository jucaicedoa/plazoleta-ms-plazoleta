package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.CrearPlatoRequestDto;
import com.plazoleta.plazoleta.application.mapper.PlatoApplicationMapper;
import com.plazoleta.plazoleta.domain.api.PlatoServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrearPlatoHandler {

    private final PlatoServicePort platoServicePort;
    private final PlatoApplicationMapper mapper;

    public void handle(CrearPlatoRequestDto dto, Long propietarioId) {
        platoServicePort.crearPlato(mapper.toDomain(dto), propietarioId);
    }
}