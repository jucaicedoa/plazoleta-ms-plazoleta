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
@Table(name = "restaurante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(name = "propietario_id", nullable = false)
    private Long propietarioId;

    @Column(nullable = false, length = 13)
    private String telefono;

    @Column(name = "url_logo", length = 500)
    private String urlLogo;

    @Column(nullable = false, unique = true, length = 20)
    private String nit;
}
