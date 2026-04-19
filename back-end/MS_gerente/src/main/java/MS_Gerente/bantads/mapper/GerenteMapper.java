package MS_Gerente.bantads.mapper;

import MS_Gerente.bantads.dto.request.AtualizaGerenteDTO;
import MS_Gerente.bantads.model.Gerente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GerenteMapper {
    void ataualizaGerentePeloDto(AtualizaGerenteDTO dto, @MappingTarget Gerente gerente);
}
