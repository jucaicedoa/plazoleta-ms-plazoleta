package com.plazoleta.plazoleta.infraestructure.out.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlatoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, length = 255)
    private String urlImagen;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false)
    private Boolean activo;

    @Column(nullable = false)
    private Long restauranteId;
}