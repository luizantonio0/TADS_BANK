package com.bantads.cliente.mapper;

import com.bantads.cliente.dto.AlterarDadosClienteDTO;
import com.bantads.cliente.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClienteMapper {
    void updateEntityFromDto(AlterarDadosClienteDTO dto, @MappingTarget Cliente entity);
}
