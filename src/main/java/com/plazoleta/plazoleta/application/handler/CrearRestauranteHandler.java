package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.application.mapper.RestauranteApplicationMapper;
import com.plazoleta.plazoleta.domain.api.RestauranteServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class CrearRestauranteHandler {

    private final RestauranteServicePort servicePort;
    private final RestauranteApplicationMapper mapper;

    public void handle(CrearRestauranteRequestDto dto) {
        servicePort.crearRestaurante(mapper.toDomain(dto));
    }
}