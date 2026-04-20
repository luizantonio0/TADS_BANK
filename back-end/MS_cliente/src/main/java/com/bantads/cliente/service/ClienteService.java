package com.bantads.cliente.service;

import com.bantads.cliente.dto.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.dto.auth.CredentialsCreateDTO;
import com.bantads.cliente.dto.conta.ContaCreateDTO;
import com.bantads.cliente.dto.orchestrator.OrchestrationCommandDTO;
import com.bantads.cliente.dto.orchestrator.OrchestrationConfirmDTO;
import com.bantads.cliente.dto.orchestrator.OrchestrationRequestDTO;
import com.bantads.cliente.exceptions.AccountAlredyExists;
import com.bantads.cliente.mapper.ClienteMapper;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.orchestration.OrchestrationKeys;
import com.bantads.cliente.repository.ClienteRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class ClienteService {

    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder encoder;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;

    public ClienteService(RabbitTemplate rabbitTemplate, ClienteRepository clienteRepository, ClienteMapper mapper, PasswordEncoder encoder) {
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
        this.encoder = encoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll(); 
    }
    
    public Cliente findByCpf(String cpf){
        return clienteRepository.findByCpf(cpf).orElse(null);
    }

    public Cliente cadastrarCliente(ClienteRequestDTO dto) throws AccountAlredyExists {
        if(clienteRepository.existsByCpf(dto.cpf())) {
            throw new AccountAlredyExists("Este CPF já está em uso!");
        }

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

    public void aprovarCliente(String cpf) throws Exception {
        var cliente = clienteRepository.findByCpf(cpf);
        if(cliente.isEmpty()) throw new IllegalStateException("Cliente não encontrado");
        var c = cliente.get();
        c.setAprovado(true);
        clienteRepository.save(c);
    }

    public void rollbackCliente(UUID uuid) throws Exception {
        Page<Revision<Integer, Cliente>> revisions = clienteRepository.findRevisions(uuid, PageRequest.of(0, 2, Sort.by("revisionNumber").descending()));
        List<Revision<Integer, Cliente>> content = revisions.getContent();

        if (content.size() >= 2) {
            var revision = content.get(1).getEntity();
            clienteRepository.save(revision);
        } else {
            clienteRepository.deleteById(uuid);
        }

    }

    public void aprovarDeprecated(String cpf, String cpfGerente) throws Exception {
        var clienteAtual = clienteRepository.findByCpf(cpf);

        if(clienteAtual.isEmpty()) throw new IllegalStateException("Cliente não encontrado");

        var cliente = clienteAtual.get();

        var numConta = ThreadLocalRandom.current().nextInt(1000, 9999)+"";
        var senha = ThreadLocalRandom.current().nextInt(1000, 9999)+"";
        var idOperation = UUID.randomUUID();

        var credentialsDTO = new CredentialsCreateDTO(cliente.getEmail(), cpf, encoder.encode(senha));
        var contaDTO = new ContaCreateDTO(numConta, cpf, cpfGerente);

        var request = new OrchestrationRequestDTO(
                idOperation,
                List.of(
                        new OrchestrationCommandDTO<>(UUID.randomUUID(), UUID.randomUUID(), OrchestrationKeys.MS_AUTH, OrchestrationKeys.CREATE_CREDENTIALS_COMMAND, credentialsDTO),
                        new OrchestrationCommandDTO<>(UUID.randomUUID(), UUID.randomUUID(), OrchestrationKeys.MS_CONTA, OrchestrationKeys.CREATE_CONTA_COMMAND, contaDTO)
                )
        );

        var response = (OrchestrationConfirmDTO) rabbitTemplate.convertSendAndReceive(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

        if(response != null && response.ok()) {
            clienteRepository.save(cliente);
            return;
        }

        throw new Exception("Erro ao tentar aprovar cliente: " + response.message());
    }
}
        