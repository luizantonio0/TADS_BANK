package com.bantads.cliente.service;

import com.bantads.cliente.dto.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.AprovarClienteDTO;
import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.dto.amqp.ContaCreateDTO;
import com.bantads.cliente.dto.amqp.CredentialsCreateDTO;
import com.bantads.cliente.exceptions.AccountAlredyExists;
import com.bantads.cliente.mapper.ClienteMapper;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.repository.ClienteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class ClienteService {

    private final PasswordEncoder encoder;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;

    private final Map<Long, OrchestratedOperation<AprovarClienteDTO>> pendingOperations = new ConcurrentHashMap<>();

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper mapper, PasswordEncoder encoder) {
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
        this.encoder = encoder;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll(); 
    }
    
    public Cliente findByCpf(String cpf){
        return clienteRepository.findByCpf(cpf).orElse(null);
    }

    public Cliente save(ClienteRequestDTO dto) throws AccountAlredyExists {
        boolean existe = clienteRepository.existsByCpf(dto.cpf());

        if(existe) throw new AccountAlredyExists("Não é possivel cadastrar um cliente com o mesmo CPF");

        Cliente cliente = new Cliente(dto);
        return clienteRepository.save(cliente);
    }
    
    public Cliente update(AlterarDadosClienteDTO dto, String cpf){
        var clienteAtual = clienteRepository.findByCpf(cpf);

        if(clienteAtual.isEmpty()) return null;

        var cliente = clienteAtual.get();

        mapper.updateEntityFromDto(dto, cliente);

        return clienteRepository.save(cliente);
    }

    public AprovarClienteDTO aprovar(String cpf, String cpfGerente){
        var clienteAtual = clienteRepository.findByCpf(cpf);

        if(clienteAtual.isEmpty()) return null;

        var cliente = clienteAtual.get();

        var numConta = ThreadLocalRandom.current().nextInt(1000, 9999);
        var senha = ThreadLocalRandom.current().nextInt(1000, 9999);

        var idOperation = ThreadLocalRandom.current().nextLong();
        var operation = new OrchestratedOperation(
                ThreadLocalRandom.current().nextLong(),
                new CountDownLatch(2),
                List.of(
                        (op) -> {

                        },
                        (op) -> {

                        }
                )
        );

        try {
            boolean sucesso = operation.countDownLatch().await(10, TimeUnit.SECONDS);
            if (!sucesso) {
                throw new RuntimeException("Timeout: Os serviços não responderam a tempo.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        var credentialsDTO = new CredentialsCreateDTO(idOperation, cliente.getEmail(), cpf, encoder.encode(senha+""));
        var contaDTO = new ContaCreateDTO(idOperation, numConta, cpf, cpfGerente);

        clienteRepository.save(cliente);

        return new AprovarClienteDTO(cliente.getCpf(), "", BigDecimal.ZERO, BigDecimal.ZERO, "","","");
    }
}
        