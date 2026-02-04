package com.plazoleta.plazoleta.infraestructure.out.jpa.adapter;

import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.spi.PlatoPersistencePort;
import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.PlatoEntity;
import com.plazoleta.plazoleta.infraestructure.out.jpa.mapper.PlatoEntityMapper;
import com.plazoleta.plazoleta.infraestructure.out.jpa.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@RequiredArgsConstructor
public class PlatoJpaAdapter implements PlatoPersistencePort {

    private final PlatoRepository platoRepository;
    private final PlatoEntityMapper platoEntityMapper;

    @Override
    public Plato getById(Long dishId) {
        Optional<PlatoEntity> entity = platoRepository.findById(dishId);
        return entity.map(platoEntityMapper::toDomain).orElse(null);
    }

    @Override
    public void save(Plato plato) {
        PlatoEntity entity = platoEntityMapper.toEntity(plato);
        platoRepository.save(entity);
    }
}