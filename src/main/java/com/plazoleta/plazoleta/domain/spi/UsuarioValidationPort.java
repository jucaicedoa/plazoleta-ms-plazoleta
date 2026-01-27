package com.plazoleta.plazoleta.domain.spi;

import com.plazoleta.plazoleta.domain.model.UsuarioModelo;

public interface UsuarioValidationPort {
    UsuarioModelo getUserById(Long userId);
}