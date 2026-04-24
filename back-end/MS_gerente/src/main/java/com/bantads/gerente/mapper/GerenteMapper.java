package com.bantads.gerente.mapper;

import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.model.Gerente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GerenteMapper {
    void ataualizaGerentePeloDto(AtualizaGerenteDTO dto, @MappingTarget Gerente gerente);
}
