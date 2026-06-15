package com.bantads.conta.service;

import com.bantads.conta.datasource.DataSourceContextHolder;
import com.bantads.conta.datasource.DataSourceType;
import com.bantads.conta.dto.*;
import com.bantads.conta.dto.cqrs.CQRSSyncEntity;
import com.bantads.conta.exception.BadRequestException;
import com.bantads.conta.exception.ForbiddenException;
import com.bantads.conta.exception.HttpException;
import com.bantads.conta.exception.NotFoundException;
import com.bantads.conta.model.Conta;
import com.bantads.conta.model.Movimentacao;
import com.bantads.conta.model.TipoMovimentacao;
import com.bantads.conta.repository.read.MovimentacaoReadRepository;
import com.bantads.conta.repository.write.ContaRepository;
import com.bantads.conta.repository.write.MovimentacaoRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MovimentacaoService {

    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private MovimentacaoReadRepository readRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    public MovimentacaoResultDTO depositar(String conta, String cpfLogado, DepositoDTO dto) throws HttpException {
        var valor = dto.valor().setScale(2, RoundingMode.HALF_UP);
        var contaDestino = contaRepository.findByConta(conta)
                .orElseThrow(() -> new BadRequestException("Conta de depósito não encontrada"));

        if (!contaDestino.getCpf().equals(cpfLogado)) {
            throw new ForbiddenException("Você não tem permissão para realizar essa operação");
        }

        contaDestino.setSaldo(contaDestino.getSaldo().add(valor).setScale(2, RoundingMode.HALF_UP));
        contaRepository.save(contaDestino);

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now().withNano(0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .valor(valor)
                .contaOrigem(conta)
                .build();

        movimentacaoRepository.save(movimentacao);
        sincronizarMovimentacao(movimentacao);
        sincronizarConta(contaDestino);

        return new MovimentacaoResultDTO(contaDestino.getConta(), movimentacao.getDataHora(), contaDestino.getSaldo(), null, null);
    }

    @Transactional
    public MovimentacaoResultDTO sacar(String conta, SaqueDTO dto) throws HttpException {
        var valor = dto.valor().setScale(2, RoundingMode.HALF_UP);
        var contaDestino = contaRepository.findByConta(conta)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        BigDecimal saldoDisponivel = contaDestino.getSaldo().add(contaDestino.getLimite());
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new BadRequestException("Saldo insuficiente (considerando limite)");
        }

        contaDestino.setSaldo(contaDestino.getSaldo().subtract(valor).setScale(2, RoundingMode.HALF_UP));

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now().withNano(0))
                .tipo(TipoMovimentacao.SAQUE)
                .valor(valor)
                .contaOrigem(conta)
                .build();

        movimentacaoRepository.save(movimentacao);
        contaRepository.save(contaDestino);

        sincronizarMovimentacao(movimentacao);
        sincronizarConta(contaDestino);

        return new MovimentacaoResultDTO(contaDestino.getConta(), movimentacao.getDataHora(), contaDestino.getSaldo(), null, null);
    }

    @Transactional
    public MovimentacaoResultDTO transferir(String conta, String cpfLogado, TransferenciaDTO dto) throws HttpException {
        var valor = dto.valor().setScale(2, RoundingMode.HALF_UP);
        var origem = contaRepository.findByConta(conta)
                .orElseThrow(() -> new BadRequestException("Conta de origem não encontrada"));

        if (!origem.getCpf().equals(cpfLogado)) {
            throw new ForbiddenException("Você não tem permissão para realizar essa operação");
        }

        var destino = contaRepository.findByConta(dto.destino())
                .orElseThrow(() -> new BadRequestException("Conta de destino não encontrada"));

        if (destino.getCpf().equals(origem.getCpf())) {
            throw new ForbiddenException("Não é permitido transferir para a própria conta");
        }

        BigDecimal saldoDisponivel = origem.getSaldo().add(origem.getLimite());
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new BadRequestException("Saldo insuficiente na conta de origem");
        }

        origem.setSaldo(origem.getSaldo().subtract(valor).setScale(2, RoundingMode.HALF_UP));
        destino.setSaldo(destino.getSaldo().add(valor).setScale(2, RoundingMode.HALF_UP));

        contaRepository.save(origem);
        contaRepository.save(destino);

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now().withNano(0))
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .valor(valor)
                .contaOrigem(conta)
                .contaDestino(dto.destino())
                .build();

        movimentacaoRepository.save(movimentacao);

        sincronizarConta(origem);
        sincronizarConta(destino);
        sincronizarMovimentacao(movimentacao);

        return new MovimentacaoResultDTO(origem.getConta(), movimentacao.getDataHora(), origem.getSaldo(), dto.valor(), destino.getConta());
    }

    protected void sincronizarMovimentacao(Movimentacao movimentacao) {
        var dto = CQRSSyncEntity.MovimentacaoDTO.from(movimentacao);
        rabbitTemplate.convertAndSend("ms-conta.cqrs.movimentacao", dto);
    }

    protected void sincronizarConta(Conta conta) {
        var dto = CQRSSyncEntity.ContaDTO.from(conta);
        rabbitTemplate.convertAndSend("ms-conta.cqrs.conta", dto);
    }

    @Transactional(readOnly = true)
    public ExtratoResponseDTO getExtrato(String numConta, LocalDate inicio, LocalDate fim) throws BadRequestException {

        var primeiraMovimentacaoOpt = movimentacaoRepository
                .findFirstByContaOrigemOrContaDestinoOrderByDataHoraAsc(numConta, numConta);
        //var ultimaMovimentacaoOpt = movimentacaoRepository.findFirstByContaOrigemOrContaDestinoOrderByDataHoraDesc(numConta, numConta);

        Optional<Conta> contaOpt = contaRepository.findByConta(numConta);
        if (contaOpt.isEmpty()) {
            throw new BadRequestException("Conta não encontrada");
        }

        if (primeiraMovimentacaoOpt.isEmpty()) {
            return new ExtratoResponseDTO(numConta, BigDecimal.ZERO, List.of(), Map.of());
        }

        var primeiraMovimentacao = primeiraMovimentacaoOpt.get();
        //var ultimaMovimentacao = ultimaMovimentacaoOpt.get();
        var conta = contaOpt.get();

        LocalDateTime dataInicio = inicio == null ? primeiraMovimentacaoOpt.get().getDataHora().toLocalDate().atStartOfDay()
                : inicio.isBefore(primeiraMovimentacao.getDataHora().toLocalDate())
                        ? primeiraMovimentacao.getDataHora().toLocalDate().atStartOfDay()
                        : inicio.atStartOfDay();
        /*LocalDateTime dataFim = fim == null ? LocalDateTime.MAX
                : fim.isAfter(ultimaMovimentacao.getDataHora().toLocalDate())
                        ? ultimaMovimentacao.getDataHora().toLocalDate().atTime(LocalTime.MAX)
                        : fim.atTime(LocalTime.MAX);*/

        LocalDateTime dataFim = fim == null ? LocalDateTime.now() : fim.isAfter(LocalDateTime.now().toLocalDate()) ? LocalDateTime.now() : fim.atTime(LocalTime.MAX);

        List<Movimentacao> anteriores = movimentacaoRepository.findByContaBefore(numConta, dataInicio);
        BigDecimal saldoAtual = BigDecimal.ZERO;

        for (Movimentacao m : anteriores) {
            if (m.getTipo() == TipoMovimentacao.DEPOSITO) {
                saldoAtual = saldoAtual.add(m.getValor());
            } else if (m.getTipo() == TipoMovimentacao.SAQUE) {
                saldoAtual = saldoAtual.subtract(m.getValor());
            } else if (m.getTipo() == TipoMovimentacao.TRANSFERENCIA) {
                if (numConta.equals(m.getContaOrigem())) {
                    saldoAtual = saldoAtual.subtract(m.getValor());
                } else {
                    saldoAtual = saldoAtual.add(m.getValor());
                }
            }
        }

        List<Movimentacao> periodo = movimentacaoRepository.findByContaAndPeriodo(numConta, dataInicio, dataFim);
        List<MovimentacaoDTO> dtos = new ArrayList<>();
        Map<String, BigDecimal> saldosDiarios = new LinkedHashMap<>();

        LocalDate dataCorrente = dataInicio.toLocalDate();
        int indexMov = 0;

        while (!dataCorrente.isAfter(dataFim.toLocalDate())) {
            while (indexMov < periodo.size()
                    && periodo.get(indexMov).getDataHora().toLocalDate().equals(dataCorrente)) {
                Movimentacao m = periodo.get(indexMov);
                BigDecimal valor = m.getValor();

                if (m.getTipo() == TipoMovimentacao.SAQUE) {
                    saldoAtual = saldoAtual.subtract(valor);
                } else if (m.getTipo() == TipoMovimentacao.DEPOSITO) {
                    saldoAtual = saldoAtual.add(valor);
                } else if (m.getTipo() == TipoMovimentacao.TRANSFERENCIA) {
                    if (numConta.equals(m.getContaOrigem())) {
                        saldoAtual = saldoAtual.subtract(valor);
                    } else {
                        saldoAtual = saldoAtual.add(valor);
                    }
                }

                dtos.add(new MovimentacaoDTO(
                        m.getDataHora().withNano(0),
                        m.getTipo().nome(),
                        m.getContaOrigem(),
                        m.getContaDestino(),
                        valor));
                indexMov++;
            }
            saldosDiarios.put(dataCorrente.toString(), saldoAtual.setScale(2, RoundingMode.HALF_UP));
            dataCorrente = dataCorrente.plusDays(1);
        }

        return new ExtratoResponseDTO(conta.getConta(), conta.getSaldo(), dtos, saldosDiarios);
    }

    public void sync(CQRSSyncEntity.MovimentacaoDTO m) {
        DataSourceContextHolder.setContext(DataSourceType.READER);
        Movimentacao c = new Movimentacao();
        c.setContaDestino(m.contaDestino());
        c.setContaOrigem(m.contaOrigem());
        c.setDataHora(m.dataHora());
        c.setId(m.id());
        c.setTipo(TipoMovimentacao.valueOf(m.tipo()));
        c.setValor(m.valor());
        readRepository.save(c);
    }

    @Transactional
    public void reboot(DataSourceType context) {
        DataSourceContextHolder.setContext(context);
        var repository = context == DataSourceType.WRITER ? movimentacaoRepository : readRepository;
        repository.deleteAll();
        repository.flush();
        Movimentacao m1 = Movimentacao.builder()
                .id(UUID.fromString("d67612cc-ea11-4667-82b2-b1b48cd6e017"))
                .dataHora(LocalDateTime.of(2020, 1, 1, 10, 0, 0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .contaOrigem("1291")
                .contaDestino(null)
                .valor(new BigDecimal("1000.00"))
                .build();

        Movimentacao m2 = Movimentacao.builder()
                .id(UUID.fromString("767eb3c8-15d0-42da-bc05-9ed9c4e9ab50"))
                .dataHora(LocalDateTime.of(2020, 1, 1, 11, 0, 0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .contaOrigem("1291")
                .contaDestino(null)
                .valor(new BigDecimal("900.00"))
                .build();

        Movimentacao m3 = Movimentacao.builder()
                .id(UUID.fromString("647c85de-b4b4-485f-a8fa-5e977810e47d"))
                .dataHora(LocalDateTime.of(2020, 1, 1, 12, 0, 0))
                .tipo(TipoMovimentacao.SAQUE)
                .contaOrigem("1291")
                .contaDestino(null)
                .valor(new BigDecimal("550.00"))
                .build();

        Movimentacao m4 = Movimentacao.builder()
                .id(UUID.fromString("c579be8a-5a8d-4960-812a-d73b49a1111a"))
                .dataHora(LocalDateTime.of(2020, 1, 1, 13, 0, 0))
                .tipo(TipoMovimentacao.SAQUE)
                .contaOrigem("1291")
                .contaDestino(null)
                .valor(new BigDecimal("350.00"))
                .build();

        Movimentacao m5 = Movimentacao.builder()
                .id(UUID.fromString("1047a46d-d329-4f98-af06-0892263bccd6"))
                .dataHora(LocalDateTime.of(2020, 1, 10, 15, 0, 0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .contaOrigem("1291")
                .contaDestino(null)
                .valor(new BigDecimal("2000.00"))
                .build();

        Movimentacao m6 = Movimentacao.builder()
                .id(UUID.fromString("9c88fbdf-0d76-4be6-b77e-9622c2be2cb6"))
                .dataHora(LocalDateTime.of(2020, 1, 15, 8, 0, 0))
                .tipo(TipoMovimentacao.SAQUE)
                .contaOrigem("1291")
                .contaDestino(null)
                .valor(new BigDecimal("500.00"))
                .build();

        Movimentacao m7 = Movimentacao.builder()
                .id(UUID.fromString("8c69b96e-7925-4cb5-8875-a47dccaabae0"))
                .dataHora(LocalDateTime.of(2020, 1, 20, 12, 0, 0))
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .contaOrigem("1291")
                .contaDestino("0950")
                .valor(new BigDecimal("1700.00"))
                .build();

        Movimentacao m8 = Movimentacao.builder()
                .id(UUID.fromString("f6fa1365-f1e9-4fc0-a8da-1716c4f765ed"))
                .dataHora(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .contaOrigem("0950")
                .contaDestino(null)
                .valor(new BigDecimal("1000.00"))
                .build();

        Movimentacao m9 = Movimentacao.builder()
                .id(UUID.fromString("93611a0a-af09-4b7f-aa92-3e633a7026ee"))
                .dataHora(LocalDateTime.of(2025, 1, 2, 10, 0, 0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .contaOrigem("0950")
                .contaDestino(null)
                .valor(new BigDecimal("5000.00"))
                .build();

        Movimentacao m10 = Movimentacao.builder()
                .id(UUID.fromString("8f2a7102-b207-4697-9f99-4b80931b272a"))
                .dataHora(LocalDateTime.of(2025, 1, 10, 10, 0, 0))
                .tipo(TipoMovimentacao.SAQUE)
                .contaOrigem("0950")
                .contaDestino(null)
                .valor(new BigDecimal("200.00"))
                .build();

        Movimentacao m11 = Movimentacao.builder()
                .id(UUID.fromString("bae41297-12b5-48d1-8edf-23f3a09aaf96"))
                .dataHora(LocalDateTime.of(2025, 2, 5, 10, 0, 0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .contaOrigem("0950")
                .contaDestino(null)
                .valor(new BigDecimal("7000.00"))
                .build();

        Movimentacao m12 = Movimentacao.builder()
                .id(UUID.fromString("685f1e32-7756-41de-9d01-e32a1a8814b6"))
                .dataHora(LocalDateTime.of(2025, 5, 5, 10, 0, 0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .contaOrigem("8573")
                .contaDestino(null)
                .valor(new BigDecimal("1000.00"))
                .build();

        Movimentacao m13 = Movimentacao.builder()
                .id(UUID.fromString("ae1a2cbb-8631-467c-aabb-3fac63acbb6b"))
                .dataHora(LocalDateTime.of(2025, 5, 6, 10, 0, 0))
                .tipo(TipoMovimentacao.SAQUE)
                .contaOrigem("8573")
                .contaDestino(null)
                .valor(new BigDecimal("2000.00"))
                .build();

        Movimentacao m14 = Movimentacao.builder()
                .id(UUID.fromString("170e5950-44a8-4f50-ad90-331c76e1b4f0"))
                .dataHora(LocalDateTime.of(2025, 1, 6, 10, 0, 0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .contaOrigem("5887")
                .contaDestino(null)
                .valor(new BigDecimal("150000.00"))
                .build();

        Movimentacao m15 = Movimentacao.builder()
                .id(UUID.fromString("0f96f1b4-81b9-4d40-a312-4c5d082c78cc"))
                .dataHora(LocalDateTime.of(2025, 1, 7, 10, 0, 0))
                .tipo(TipoMovimentacao.DEPOSITO)
                .contaOrigem("7617")
                .contaDestino(null)
                .valor(new BigDecimal("1500.00"))
                .build();

        repository.saveAll(List.of(m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15));
    }
}
