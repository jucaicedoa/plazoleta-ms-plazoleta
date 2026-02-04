package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.application.mapper.RestauranteApplicationMapper;
import com.plazoleta.plazoleta.domain.api.RestauranteServicePort;

public class RestauranteHandler implements IRestauranteHandler {

    private final RestauranteServicePort servicePort;
    private final RestauranteApplicationMapper mapper;

    public RestauranteHandler(RestauranteServicePort servicePort, RestauranteApplicationMapper mapper) {
        this.servicePort = servicePort;
        this.mapper = mapper;
    }

    @Override
    public void createRestaurante(CrearRestauranteRequestDto dto) {
        servicePort.crearRestaurante(mapper.toDomain(dto));
    }
}