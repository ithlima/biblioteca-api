package com.mej.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mej.biblioteca.model.entity.Livro;
import com.mej.biblioteca.model.entity.Usuario;
import com.mej.biblioteca.model.enums.Role;
import com.mej.biblioteca.repository.EmprestimoRepository;
import com.mej.biblioteca.repository.LivroRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private LivroService livroService;

    @Test
    void listarAdminPodeFiltrarPorOculto() {
        UUID categoriaId = UUID.randomUUID();
        Boolean disponivel = true;
        Boolean oculto = true;
        Pageable pageable = PageRequest.of(0, 10);
        
        Authentication authAdmin = new UsernamePasswordAuthenticationToken(
                "admin@teste.com", "senha", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Livro livro = Livro.builder().id(UUID.randomUUID()).oculto(true).build();
        when(livroRepository.findComFiltros(categoriaId, disponivel, true, pageable))
                .thenReturn(new PageImpl<>(List.of(livro)));
        when(emprestimoRepository.countByLivroIdAndStatusIn(any(), any())).thenReturn(0L);

        Page<Object> result = livroService.listar(categoriaId, disponivel, oculto, pageable, authAdmin);

        assertEquals(1, result.getTotalElements());
        verify(livroRepository).findComFiltros(categoriaId, disponivel, true, pageable);
    }

    @Test
    void listarLeitorNaoPodeFiltrarPorOcultoForcaFalso() {
        UUID categoriaId = UUID.randomUUID();
        Boolean disponivel = true;
        Boolean oculto = true; // Mesmo passando oculto=true na request, service deve forçar oculto=false para não-admins
        Pageable pageable = PageRequest.of(0, 10);
        
        Authentication authLeitor = new UsernamePasswordAuthenticationToken(
                "leitor@teste.com", "senha", List.of(new SimpleGrantedAuthority("ROLE_LEITOR")));

        Livro livro = Livro.builder().id(UUID.randomUUID()).oculto(false).build();
        when(livroRepository.findComFiltros(categoriaId, disponivel, false, pageable))
                .thenReturn(new PageImpl<>(List.of(livro)));
        when(emprestimoRepository.countByLivroIdAndStatusIn(any(), any())).thenReturn(0L);

        Page<Object> result = livroService.listar(categoriaId, disponivel, oculto, pageable, authLeitor);

        assertEquals(1, result.getTotalElements());
        // Aqui validamos a principal regra de segurança do filtro: foi forçado falso!
        verify(livroRepository).findComFiltros(categoriaId, disponivel, false, pageable);
    }
}
