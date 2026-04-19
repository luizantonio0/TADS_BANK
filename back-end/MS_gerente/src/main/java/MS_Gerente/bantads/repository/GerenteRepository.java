package MS_Gerente.bantads.repository;

import MS_Gerente.bantads.model.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, UUID> {
    Gerente findByCpf(String cpf);

    void deleteByCpf(String cpf);
} 
        