package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.CategoriaRequest;
import com.mej.biblioteca.dto.CategoriaResponse;
import com.mej.biblioteca.exception.CategoriaNomeDuplicadoException;
import com.mej.biblioteca.exception.CategoriaNotFoundException;
import com.mej.biblioteca.exception.CategoriaVinculadaLivroException;
import com.mej.biblioteca.model.Categoria;
import com.mej.biblioteca.repository.CategoriaRepository;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        String nome = normalizarObrigatorio(request.nome());
        validarNomeDuplicado(nome, null);
        Categoria categoria = Categoria.builder()
                .nome(nome)
                .descricao(normalizarOpcional(request.descricao()))
                .build();
        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaResponse::from)
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.nome(), b.nome()))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscar(UUID id) {
        return CategoriaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public CategoriaResponse atualizar(UUID id, CategoriaRequest request) {
        Categoria categoria = buscarEntidade(id);
        String nome = normalizarObrigatorio(request.nome());
        validarNomeDuplicado(nome, id);
        categoria.setNome(nome);
        categoria.setDescricao(normalizarOpcional(request.descricao()));
        return CategoriaResponse.from(categoria);
    }

    @Transactional
    public void remover(UUID id) {
        Categoria categoria = buscarEntidade(id);
        if (categoriaRepository.existsByIdAndLivrosIsNotEmpty(id)) {
            throw new CategoriaVinculadaLivroException();
        }
        categoriaRepository.delete(categoria);
    }

    @Transactional(readOnly = true)
    public Set<Categoria> buscarEntidades(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Set<UUID> idsUnicos = new LinkedHashSet<>(ids);
        List<Categoria> categorias = categoriaRepository.findAllById(idsUnicos);
        if (categorias.size() != idsUnicos.size()) {
            Set<UUID> idsEncontrados = categorias.stream()
                    .map(Categoria::getId)
                    .collect(java.util.stream.Collectors.toSet());
            UUID idNaoEncontrado = idsUnicos.stream()
                    .filter(id -> !idsEncontrados.contains(id))
                    .findFirst()
                    .orElseThrow();
            throw new CategoriaNotFoundException("Categoria não encontrada: " + idNaoEncontrado + ".");
        }
        return new LinkedHashSet<>(categorias);
    }

    @Transactional(readOnly = true)
    public Categoria buscarEntidade(UUID id) {
        return categoriaRepository.findById(id)
                .orElseThrow(CategoriaNotFoundException::new);
    }

    private void validarNomeDuplicado(String nome, UUID idAtual) {
        boolean duplicado = idAtual == null
                ? categoriaRepository.existsByNomeIgnoreCase(nome)
                : categoriaRepository.existsByNomeIgnoreCaseAndIdNot(nome, idAtual);
        if (duplicado) {
            throw new CategoriaNomeDuplicadoException();
        }
    }

    private String normalizarObrigatorio(String valor) {
        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
