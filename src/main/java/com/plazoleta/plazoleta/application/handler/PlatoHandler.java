package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.ActualizarPlatoRequestDto;
import com.plazoleta.plazoleta.application.dto.CrearPlatoRequestDto;
import com.plazoleta.plazoleta.application.mapper.PlatoApplicationMapper;
import com.plazoleta.plazoleta.application.security.ICurrentUserProvider;
import com.plazoleta.plazoleta.domain.api.PlatoServicePort;
import com.plazoleta.plazoleta.domain.api.PlatoUpdateServicePort;

public class PlatoHandler implements IPlatoHandler {

    private final PlatoServicePort platoServicePort;
    private final PlatoUpdateServicePort platoUpdateServicePort;
    private final PlatoApplicationMapper mapper;
    private final ICurrentUserProvider currentUserProvider;

    public PlatoHandler(PlatoServicePort platoServicePort,
                        PlatoUpdateServicePort platoUpdateServicePort,
                        PlatoApplicationMapper mapper,
                        ICurrentUserProvider currentUserProvider) {
        this.platoServicePort = platoServicePort;
        this.platoUpdateServicePort = platoUpdateServicePort;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public void createDish(CrearPlatoRequestDto dto) {
        Long propietarioId = currentUserProvider.getCurrentUserId();
        platoServicePort.crearPlato(mapper.toDomain(dto), propietarioId);
    }

    @Override
    public void updateDish(Long platoId, ActualizarPlatoRequestDto dto) {
        Long propietarioId = currentUserProvider.getCurrentUserId();
        platoUpdateServicePort.updateDish(platoId, dto.getPrecio(), dto.getDescripcion(), propietarioId);
    }
}