package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.mej.biblioteca.dto.emprestimo.EmprestimoSolicitarRequest;
import com.mej.biblioteca.exception.domain.AcessoNegadoException;
import com.mej.biblioteca.exception.domain.EmprestimoNotFoundException;
import com.mej.biblioteca.exception.domain.EmprestimoOperacaoInvalidaException;
import com.mej.biblioteca.exception.domain.LivroIndisponivelException;
import com.mej.biblioteca.exception.domain.UsuarioComPenalidadeAtivaException;
import com.mej.biblioteca.exception.domain.UsuarioComEmprestimoAtivoException;
import com.mej.biblioteca.model.entity.Emprestimo;
import com.mej.biblioteca.model.entity.Livro;
import com.mej.biblioteca.model.entity.Penalidade;
import com.mej.biblioteca.model.enums.StatusEmprestimo;
import com.mej.biblioteca.model.entity.Usuario;
import com.mej.biblioteca.repository.EmprestimoRepository;
import com.mej.biblioteca.repository.PenalidadeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private static final int PRAZO_EMPRESTIMO_DIAS = 15;
    private static final int PRAZO_PENALIDADE_DIAS = 7;
    private static final String MOTIVO_ATRASO = "Atraso na devolucao do livro";
    private static final int LIMITE_RENOVACOES = 5;
    private static final List<StatusEmprestimo> STATUS_ATIVOS = List.of(
            StatusEmprestimo.SOLICITADO,
            StatusEmprestimo.EMPRESTADO,
            StatusEmprestimo.ATRASADO
    );

    private final EmprestimoRepository emprestimoRepository;
    private final PenalidadeRepository penalidadeRepository;
    private final LivroService livroService;
    private final UsuarioService usuarioService;

    @Transactional
    public EmprestimoResponse solicitar(EmprestimoSolicitarRequest request, Authentication authentication) {
        Usuario leitor = usuarioService.usuarioAutenticado(authentication);
        Livro livro = livroService.buscarEntidade(request.livroId());

        if (livro.getOculto()) {
            throw new LivroIndisponivelException("Livro indisponível para solicitação.");
        }
        if (emprestimoRepository.existsByLeitorAndStatusIn(leitor, STATUS_ATIVOS)) {
            throw new UsuarioComEmprestimoAtivoException();
        }
        if (temPenalidadeAtiva(leitor)) {
            throw new UsuarioComPenalidadeAtivaException();
        }
        if (livro.getQuantidade() <= 0) {
            throw new LivroIndisponivelException("Livro sem quantidade disponível.");
        }

        Emprestimo emprestimo = Emprestimo.builder()
                .livro(livro)
                .leitor(leitor)
                .status(StatusEmprestimo.SOLICITADO)
                .quantidadeRenovacoes(0)
                .build();

        return EmprestimoResponse.from(emprestimoRepository.save(emprestimo));
    }

    @Transactional
    public EmprestimoResponse emprestar(UUID id) {
        Emprestimo emprestimo = buscarEntidade(id);
        if (emprestimo.getStatus() != StatusEmprestimo.SOLICITADO) {
            throw new EmprestimoOperacaoInvalidaException("Somente solicitações pendentes podem ser emprestadas.");
        }

        Livro livro = emprestimo.getLivro();
        if (livro.getQuantidade() <= 0) {
            throw new LivroIndisponivelException("Livro sem quantidade disponível.");
        }

        LocalDate dataEmprestimo = LocalDate.now();

        livro.setQuantidade(livro.getQuantidade() - 1);
        emprestimo.setStatus(StatusEmprestimo.EMPRESTADO);
        emprestimo.setDataEmprestimo(dataEmprestimo);
        emprestimo.setDataDevolucaoPrevista(dataEmprestimo.plusDays(PRAZO_EMPRESTIMO_DIAS));

        return EmprestimoResponse.from(emprestimo);
    }

    @Transactional
    public EmprestimoResponse renovar(UUID id, Authentication authentication) {
        Emprestimo emprestimo = buscarEntidade(id);
        Usuario usuario = usuarioService.usuarioAutenticado(authentication);
        validarDonoOuAdmin(emprestimo, usuario);

        if (emprestimo.getStatus() != StatusEmprestimo.EMPRESTADO) {
            throw new EmprestimoOperacaoInvalidaException("Somente empréstimos em andamento podem ser renovados.");
        }
        if (emprestimo.getDataDevolucaoPrevista().isBefore(LocalDate.now())) {
            throw new EmprestimoOperacaoInvalidaException("Empréstimo fora do prazo não pode ser renovado.");
        }
        if (emprestimo.getQuantidadeRenovacoes() >= LIMITE_RENOVACOES) {
            throw new EmprestimoOperacaoInvalidaException("Limite máximo de renovações atingido.");
        }

        emprestimo.setQuantidadeRenovacoes(emprestimo.getQuantidadeRenovacoes() + 1);
        emprestimo.setDataDevolucaoPrevista(emprestimo.getDataDevolucaoPrevista().plusDays(PRAZO_EMPRESTIMO_DIAS));
        return EmprestimoResponse.from(emprestimo);
    }

    @Transactional
    public EmprestimoResponse devolver(UUID id, Authentication authentication) {
        Emprestimo emprestimo = buscarEntidade(id);
        Usuario usuario = usuarioService.usuarioAutenticado(authentication);
        validarDonoOuAdmin(emprestimo, usuario);

        if (emprestimo.getStatus() != StatusEmprestimo.EMPRESTADO && emprestimo.getStatus() != StatusEmprestimo.ATRASADO) {
            throw new EmprestimoOperacaoInvalidaException("Somente empréstimos em andamento podem ser devolvidos.");
        }

        Livro livro = emprestimo.getLivro();
        livro.setQuantidade(livro.getQuantidade() + 1);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimo.setDataDevolucaoReal(LocalDate.now());
        return EmprestimoResponse.from(emprestimo);
    }

    @Transactional(readOnly = true)
    public EmprestimoResponse obterAtual(Authentication authentication) {
        Usuario leitor = usuarioService.usuarioAutenticado(authentication);
        return emprestimoRepository.findFirstByLeitorAndStatusIn(leitor, List.of(StatusEmprestimo.EMPRESTADO, StatusEmprestimo.ATRASADO))
                .map(EmprestimoResponse::from)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponse> meuHistorico(Pageable pageable, Authentication authentication) {
        Usuario leitor = usuarioService.usuarioAutenticado(authentication);
        return emprestimoRepository.findByLeitorAndStatusInOrderByDataPedidoDesc(leitor, List.of(StatusEmprestimo.DEVOLVIDO, StatusEmprestimo.CANCELADO), pageable)
                .map(EmprestimoResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponse> minhasSolicitacoes(Pageable pageable, Authentication authentication) {
        Usuario leitor = usuarioService.usuarioAutenticado(authentication);
        return emprestimoRepository.findByLeitorAndStatusInOrderByDataPedidoDesc(leitor, List.of(StatusEmprestimo.SOLICITADO), pageable)
                .map(EmprestimoResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponse> listarSolicitacoesGerais(Pageable pageable) {
        return emprestimoRepository.findByStatusInOrderByDataPedidoDesc(List.of(StatusEmprestimo.SOLICITADO), pageable)
                .map(EmprestimoResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponse> listarAtivos(Pageable pageable) {
        return emprestimoRepository.findByStatusInOrderByDataPedidoDesc(List.of(StatusEmprestimo.EMPRESTADO, StatusEmprestimo.ATRASADO), pageable)
                .map(EmprestimoResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponse> listarHistoricoGeral(Pageable pageable) {
        return emprestimoRepository.findByStatusInOrderByDataPedidoDesc(List.of(StatusEmprestimo.DEVOLVIDO, StatusEmprestimo.CANCELADO), pageable)
                .map(EmprestimoResponse::from);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void verificarAtrasosAutomaticamente() {
        LocalDate hoje = LocalDate.now();
        emprestimoRepository.findByStatus(StatusEmprestimo.EMPRESTADO)
                .stream()
                .filter(emprestimo -> emprestimo.getDataDevolucaoPrevista() != null)
                .filter(emprestimo -> emprestimo.getDataDevolucaoPrevista().isBefore(hoje))
                .forEach(emprestimo -> {
                    emprestimo.setStatus(StatusEmprestimo.ATRASADO);

                    if (!penalidadeRepository.existsByEmprestimoIdAndAtivaTrue(emprestimo.getId())) {
                        Penalidade penalidade = Penalidade.builder()
                                .usuario(emprestimo.getLeitor())
                                .emprestimo(emprestimo)
                                .motivo(MOTIVO_ATRASO)
                                .dataInicio(hoje)
                                .dataFim(hoje.plusDays(PRAZO_PENALIDADE_DIAS))
                                .ativa(true)
                                .build();
                        penalidadeRepository.save(penalidade);
                    }
                });
    }

    private Emprestimo buscarEntidade(UUID id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(EmprestimoNotFoundException::new);
    }

    private boolean temPenalidadeAtiva(Usuario usuario) {
        return penalidadeRepository.existsByUsuarioIdAndAtivaTrue(usuario.getId());
    }

    private void validarDonoOuAdmin(Emprestimo emprestimo, Usuario usuario) {
        boolean admin = usuario.getRole().name().equals("ADMIN");
        boolean dono = emprestimo.getLeitor().getId().equals(usuario.getId());
        if (!admin && !dono) {
            throw new AcessoNegadoException();
        }
    }
}
