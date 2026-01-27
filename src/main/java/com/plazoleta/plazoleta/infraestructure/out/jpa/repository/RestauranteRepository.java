package com.plazoleta.plazoleta.infraestructure.out.jpa.repository;

import com.plazoleta.plazoleta.infraestructure.out.jpa.entity.RestauranteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<RestauranteEntity, Long> {
}