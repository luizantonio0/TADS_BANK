package MS_Gerente.bantads.model;

import MS_Gerente.bantads.dto.request.CriaGerenteDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import MS_Gerente.bantads.dto.request.AtualizaGerenteDTO;
import MS_Gerente.bantads.enums.GerenteTipo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tb_gerente")
@Getter
@Setter
public class Gerente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 20)
    @NotBlank(message = "Nome não pode ser vazio")
    @Min(value = 3, message = "Nome deve ter pelo menos 3 caracteres")
    private String nome;
    @Column(nullable = false, length = 11)
    private String cpf;
    @Column(nullable = false, length = 128)
    @NotBlank(message = "Email não pode ser vazio")
    @Min(value = 3, message = "Email deve ter pelo menos 3 caracteres")
    @Email
    private String email;
    @Column(nullable = false)
    @NotBlank(message = "Senha não pode ser vazio")
    private String senha;
    @Column(nullable = false)
    private String tipo;

    public Gerente() {
    }
    public Gerente(AtualizaGerenteDTO atualizaGerenteDTO) {
        this.nome = atualizaGerenteDTO.nome();
        this.email = atualizaGerenteDTO.email();
        this.senha = atualizaGerenteDTO.senha();
    }
    public Gerente(CriaGerenteDTO criaGerenteDTO) {
        this.nome = criaGerenteDTO.nome();
        this.cpf = criaGerenteDTO.cpf();
        this.email = criaGerenteDTO.email();
        this.senha = criaGerenteDTO.senha();
        this.tipo = criaGerenteDTO.tipo().name();
    }
    public void setTipo(GerenteTipo tipo) {
        this.tipo = tipo.name();
    }

}
        