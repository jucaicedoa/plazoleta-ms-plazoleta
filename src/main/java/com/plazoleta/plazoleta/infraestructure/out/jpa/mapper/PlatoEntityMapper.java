package com.plazoleta.plazoleta.infraestructure.out.jpa.mapper;

import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.PlatoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatoEntityMapper {

    PlatoEntity toEntity(Plato dish);
    Plato toDomain(PlatoEntity entity);
}