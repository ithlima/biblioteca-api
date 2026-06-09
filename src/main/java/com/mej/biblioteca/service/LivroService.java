package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.LivroCatalogoResponse;
import com.mej.biblioteca.dto.LivroOcultarRequest;
import com.mej.biblioteca.dto.LivroRequest;
import com.mej.biblioteca.dto.LivroResponse;
import com.mej.biblioteca.exception.ConflictException;
import com.mej.biblioteca.exception.LivroNotFoundException;
import com.mej.biblioteca.model.Livro;
import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.StatusEmprestimo;
import com.mej.biblioteca.model.Usuario;
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
    public Page<Object> listar(UUID categoriaId, Pageable pageable, Authentication authentication) {
        if (isAdmin(authentication)) {
            if (categoriaId != null) {
                return livroRepository.findByCategoriasId(categoriaId, pageable)
                        .map(LivroResponse::from);
            }
            return livroRepository.findAll(pageable)
                    .map(LivroResponse::from);
        }

        if (categoriaId != null) {
            return livroRepository.findByOcultoFalseAndCategoriasId(categoriaId, pageable)
                    .map(LivroCatalogoResponse::from);
        }
        return livroRepository.findByOcultoFalse(pageable)
                .map(LivroCatalogoResponse::from);
    }

    @Transactional(readOnly = true)
    public Object buscar(UUID id, Authentication authentication) {
        Livro livro = buscarEntidade(id);
        if (isAdmin(authentication)) {
            return LivroResponse.from(livro);
        }
        if (livro.getOculto()) {
            throw new LivroNotFoundException();
        }
        return LivroCatalogoResponse.from(livro);
    }

    @Transactional
    public LivroResponse criar(LivroRequest request, Authentication authentication) {
        Usuario admin = usuarioService.usuarioAutenticado(authentication);
        validarLivroDuplicado(request, null);
        Livro livro = Livro.builder()
                .nomeObra(request.nomeObra())
                .autor(request.autor())
                .editora(request.editora())
                .volume(request.volume())
                .descricao(request.descricao())
                .categorias(categoriaService.buscarEntidades(request.categoriasIds()))
                .quantidade(request.quantidade())
                .fotoCapaUrl(request.fotoCapaUrl())
                .oculto(false)
                .criadoPor(admin)
                .build();
        return LivroResponse.from(livroRepository.save(livro));
    }

    @Transactional
    public LivroResponse atualizar(UUID id, LivroRequest request, Authentication authentication) {
        Usuario admin = usuarioService.usuarioAutenticado(authentication);
        Livro livro = buscarEntidade(id);
        validarLivroDuplicado(request, id);
        livro.setNomeObra(request.nomeObra());
        livro.setAutor(request.autor());
        livro.setEditora(request.editora());
        livro.setVolume(request.volume());
        livro.setDescricao(request.descricao());
        livro.setCategorias(categoriaService.buscarEntidades(request.categoriasIds()));
        livro.setQuantidade(request.quantidade());
        livro.setFotoCapaUrl(request.fotoCapaUrl());
        livro.setEditadoPor(admin);
        return LivroResponse.from(livro);
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
        return LivroResponse.from(livro);
    }

    @Transactional
    public LivroResponse disponibilizar(UUID id, Authentication authentication) {
        Usuario admin = usuarioService.usuarioAutenticado(authentication);
        Livro livro = buscarEntidade(id);
        livro.setOculto(false);
        livro.setMotivoOcultacao(null);
        livro.setEditadoPor(admin);
        return LivroResponse.from(livro);
    }

    @Transactional(readOnly = true)
    public Livro buscarEntidade(UUID id) {
        return livroRepository.findById(id)
                .orElseThrow(LivroNotFoundException::new);
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + Role.ADMIN.name()));
    }

    private void validarLivroDuplicado(LivroRequest request, UUID idAtual) {
        boolean duplicado = idAtual == null
                ? livroRepository.existsDuplicado(
                request.nomeObra(), request.autor(), request.editora(), request.volume())
                : livroRepository.existsDuplicadoEmOutroLivro(
                request.nomeObra(), request.autor(), request.editora(), request.volume(), idAtual);

        if (duplicado) {
            throw new ConflictException("Já existe livro cadastrado com mesma obra, autor, editora e volume.");
        }
    }
}
