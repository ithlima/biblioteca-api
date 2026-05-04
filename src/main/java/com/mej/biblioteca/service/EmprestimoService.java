package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.EmprestimoResponse;
import com.mej.biblioteca.dto.EmprestimoSolicitarRequest;
import com.mej.biblioteca.exception.BusinessException;
import com.mej.biblioteca.exception.NotFoundException;
import com.mej.biblioteca.model.Emprestimo;
import com.mej.biblioteca.model.Livro;
import com.mej.biblioteca.model.Penalidade;
import com.mej.biblioteca.model.StatusEmprestimo;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.EmprestimoRepository;
import com.mej.biblioteca.repository.PenalidadeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private static final int PRAZO_EMPRESTIMO_DIAS = 15;
    private static final int PRAZO_PENALIDADE_DIAS = 7;
    private static final String MOTIVO_ATRASO = "Atraso na devolução do livro";
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
            throw new BusinessException("Livro indisponivel para solicitacao.");
        }
        if (emprestimoRepository.existsByLeitorAndStatusIn(leitor, STATUS_ATIVOS)) {
            throw new BusinessException("Leitor ja possui emprestimo ativo.");
        }
        if (temPenalidadeAtiva(leitor)) {
            throw new BusinessException("Leitor possui penalidade ativa e não pode solicitar empréstimos.");
        }
        if (livro.getQuantidade() <= 0) {
            throw new BusinessException("Livro sem quantidade disponivel.");
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
            throw new BusinessException("Somente solicitacoes pendentes podem ser emprestadas.");
        }

        Livro livro = emprestimo.getLivro();
        if (livro.getQuantidade() <= 0) {
            throw new BusinessException("Livro sem quantidade disponivel.");
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
            throw new BusinessException("Somente emprestimos em andamento podem ser renovados.");
        }
        if (emprestimo.getDataDevolucaoPrevista().isBefore(LocalDate.now())) {
            throw new BusinessException("Emprestimo fora do prazo nao pode ser renovado.");
        }
        if (emprestimo.getQuantidadeRenovacoes() >= LIMITE_RENOVACOES) {
            throw new BusinessException("Limite maximo de renovacoes atingido.");
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
            throw new BusinessException("Somente emprestimos em andamento podem ser devolvidos.");
        }

        Livro livro = emprestimo.getLivro();
        livro.setQuantidade(livro.getQuantidade() + 1);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimo.setDataDevolucaoReal(LocalDate.now());
        return EmprestimoResponse.from(emprestimo);
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponse> meus(Authentication authentication) {
        Usuario leitor = usuarioService.usuarioAutenticado(authentication);
        return emprestimoRepository.findByLeitorOrderByDataPedidoDesc(leitor)
                .stream()
                .map(EmprestimoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponse> listar() {
        return emprestimoRepository.findAll()
                .stream()
                .map(EmprestimoResponse::from)
                .toList();
    }

    @Transactional
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
                .orElseThrow(() -> new NotFoundException("Emprestimo nao encontrado."));
    }

    private boolean temPenalidadeAtiva(Usuario usuario) {
        return penalidadeRepository.existsByUsuarioIdAndAtivaTrue(usuario.getId());
    }

    private void validarDonoOuAdmin(Emprestimo emprestimo, Usuario usuario) {
        boolean admin = usuario.getRole().name().equals("ADMIN");
        boolean dono = emprestimo.getLeitor().getId().equals(usuario.getId());
        if (!admin && !dono) {
            throw new BusinessException("Usuario nao autorizado para este emprestimo.");
        }
    }
}
