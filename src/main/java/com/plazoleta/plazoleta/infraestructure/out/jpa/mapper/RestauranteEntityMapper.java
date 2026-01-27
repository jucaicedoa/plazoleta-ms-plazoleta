package com.plazoleta.plazoleta.infraestructure.out.jpa.mapper;

import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.RestauranteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestauranteEntityMapper {
    RestauranteEntity toEntity(Restaurante restaurant);
    Restaurante toDomain(RestauranteEntity entity);
}