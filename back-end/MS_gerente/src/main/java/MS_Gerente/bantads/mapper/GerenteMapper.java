package main.java.MS_Gerente.bantads.mapper;

import main.java.MS_Gerente.bantads.dto.request.AtualizaGerenteDTO;
import main.java.MS_Gerente.bantads.model.Gerente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GerenteMapper {
    void ataualizaGerentePeloDto(AtualizaGerenteDTO dto, @MappingTarget Gerente gerente);
}
