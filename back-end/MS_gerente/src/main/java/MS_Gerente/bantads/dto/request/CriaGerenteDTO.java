package MS_Gerente.bantads.dto.request;

public record CriaGerenteDTO(
        String nome,
        String email,
        String cpf,
        String senha,
        MS_Gerente.bantads.enums.GerenteTipo tipo
) {

}
