package com.plazoleta.plazoleta.infraestructure.out.jpa.adapter;

import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.spi.PlatoPersistencePort;
import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.PlatoEntity;
import com.plazoleta.plazoleta.infraestructure.out.jpa.mapper.PlatoEntityMapper;
import com.plazoleta.plazoleta.infraestructure.out.jpa.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlatoJpaAdapter implements PlatoPersistencePort {

    private final PlatoRepository platoRepository;
    private final PlatoEntityMapper platoEntityMapper;

    @Override
    public void save(Plato plato) {
        PlatoEntity entity = platoEntityMapper.toEntity(plato);
        platoRepository.save(entity);
    }
}