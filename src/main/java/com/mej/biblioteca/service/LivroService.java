package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.livro.LivroCatalogoResponse;
import com.mej.biblioteca.dto.livro.LivroOcultarRequest;
import com.mej.biblioteca.dto.livro.LivroRequest;
import com.mej.biblioteca.dto.livro.LivroResponse;
import com.mej.biblioteca.exception.ConflictException;
import com.mej.biblioteca.exception.domain.LivroNotFoundException;
import com.mej.biblioteca.model.entity.Livro;
import com.mej.biblioteca.model.enums.Role;
import com.mej.biblioteca.model.enums.StatusEmprestimo;
import com.mej.biblioteca.model.entity.Usuario;
import com.mej.biblioteca.repository.EmprestimoRepository;
import com.mej.biblioteca.repository.LivroRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("java:S6809")
public class LivroService {

    private static final List<StatusEmprestimo> STATUS_COM_LIVRO_FORA = List.of(StatusEmprestimo.EMPRESTADO, StatusEmprestimo.ATRASADO);

    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    @Transactional(readOnly = true)
    public Page<Object> listar(UUID categoriaId, Boolean disponivel, Boolean oculto, Pageable pageable, Authentication authentication) {
        Boolean filtroOculto = isAdmin(authentication) ? oculto : false;
        return livroRepository.findComFiltros(categoriaId, disponivel, filtroOculto, pageable)
                .map(livro -> {
                    if (isAdmin(authentication)) {
                        return LivroResponse.from(livro, contarEmprestados(livro.getId()));
                    } else {
                        return LivroCatalogoResponse.from(livro, contarEmprestados(livro.getId()));
                    }
                });
    }

    @Transactional(readOnly = true)
    public Object buscar(UUID id, Authentication authentication) {
        Livro livro = buscarEntidade(id);
        if (isAdmin(authentication)) {
            return LivroResponse.from(livro, contarEmprestados(livro.getId()));
        }
        if (livro.getOculto()) {
            throw new LivroNotFoundException();
        }
        return LivroCatalogoResponse.from(livro, contarEmprestados(livro.getId()));
    }

    @Transactional
    public LivroResponse criar(LivroRequest request, Authentication authentication) {
        Usuario admin = usuarioService.usuarioAutenticado(authentication);
        String volumeTratado = getVolumeTratado(request.volume());
        validarLivroDuplicado(request, volumeTratado, null);
        Livro livro = Livro.builder()
                .nomeObra(request.nomeObra())
                .autor(request.autor())
                .editora(request.editora())
                .volume(volumeTratado)
                .descricao(request.descricao())
                .categorias(categoriaService.buscarEntidades(request.categoriasIds()))
                .quantidade(request.quantidade())
                .fotoCapaUrl(request.fotoCapaUrl())
                .oculto(false)
                .criadoPor(admin)
                .build();
        return LivroResponse.from(livroRepository.save(livro), 0);
    }

    @Transactional
    public LivroResponse atualizar(UUID id, LivroRequest request, Authentication authentication) {
        Usuario admin = usuarioService.usuarioAutenticado(authentication);
        Livro livro = buscarEntidade(id);
        String volumeTratado = getVolumeTratado(request.volume());
        validarLivroDuplicado(request, volumeTratado, id);
        livro.setNomeObra(request.nomeObra());
        livro.setAutor(request.autor());
        livro.setEditora(request.editora());
        livro.setVolume(volumeTratado);
        livro.setDescricao(request.descricao());
        livro.setCategorias(categoriaService.buscarEntidades(request.categoriasIds()));
        livro.setQuantidade(request.quantidade());
        livro.setFotoCapaUrl(request.fotoCapaUrl());
        livro.setEditadoPor(admin);
        return LivroResponse.from(livro, contarEmprestados(livro.getId()));
    }

    @Transactional
    public void remover(UUID id) {
        Livro livro = buscarEntidade(id);
        if (emprestimoRepository.existsByLivroIdAndStatusIn(id, STATUS_COM_LIVRO_FORA)) {
            throw new ConflictException("Não é permitido remover livro que esteja emprestado.");
        }
        if (emprestimoRepository.existsByLivroId(id)) {
            throw new ConflictException("Não é permitido remover livro com histórico de empréstimos.");
        }
        livroRepository.delete(livro);
    }

    @Transactional
    public LivroResponse ocultar(UUID id, LivroOcultarRequest request, Authentication authentication) {
        Usuario admin = usuarioService.usuarioAutenticado(authentication);
        Livro livro = buscarEntidade(id);
        livro.setOculto(true);
        livro.setMotivoOcultacao(request.motivoOcultacao());
        livro.setEditadoPor(admin);
        return LivroResponse.from(livro, contarEmprestados(livro.getId()));
    }

    @Transactional
    public LivroResponse disponibilizar(UUID id, Authentication authentication) {
        Usuario admin = usuarioService.usuarioAutenticado(authentication);
        Livro livro = buscarEntidade(id);
        livro.setOculto(false);
        livro.setMotivoOcultacao(null);
        livro.setEditadoPor(admin);
        return LivroResponse.from(livro, contarEmprestados(livro.getId()));
    }

    @Transactional(readOnly = true)
    public Livro buscarEntidade(UUID id) {
        return livroRepository.findById(id)
                .orElseThrow(LivroNotFoundException::new);
    }

    private Integer contarEmprestados(UUID livroId) {
        return (int) emprestimoRepository.countByLivroIdAndStatusIn(livroId, STATUS_COM_LIVRO_FORA);
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + Role.ADMIN.name()));
    }

    private void validarLivroDuplicado(LivroRequest request, String volumeTratado, UUID idAtual) {
        boolean duplicado = idAtual == null
                ? livroRepository.existsDuplicado(
                request.nomeObra(), request.autor(), request.editora(), volumeTratado)
                : livroRepository.existsDuplicadoEmOutroLivro(
                request.nomeObra(), request.autor(), request.editora(), volumeTratado, idAtual);

        if (duplicado) {
            throw new ConflictException("Já existe livro cadastrado com mesma obra, autor, editora e volume.");
        }
    }

    private String getVolumeTratado(String volume) {
        if (volume == null || volume.trim().isEmpty()) {
            return "Volume único";
        }
        return "Vol. " + volume.trim();
    }
}
