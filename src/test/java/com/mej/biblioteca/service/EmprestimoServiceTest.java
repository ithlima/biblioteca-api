package com.mej.biblioteca.service;

import com.mej.biblioteca.model.Emprestimo;
import com.mej.biblioteca.model.StatusEmprestimo;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.EmprestimoRepository;
import com.mej.biblioteca.repository.PenalidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private PenalidadeRepository penalidadeRepository;

    @Mock
    private LivroService livroService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private Usuario leitor;

    @BeforeEach
    void setUp() {
        leitor = Usuario.builder()
                .id(UUID.randomUUID())
                .nomeCompleto("Leitor Teste")
                .build();
    }

    @Test
    @DisplayName("Deve mudar status para ATRASADO e criar Penalidade quando dataDevolucaoPrevista for no passado")
    @SuppressWarnings("java:S8692")
    void testVerificarAtrasosAutomaticamente_ComAtraso_NaoTemPenalidade() {
        // Arrange
        Emprestimo emprestimoVencido = Emprestimo.builder()
                .id(UUID.randomUUID())
                .leitor(leitor)
                .status(StatusEmprestimo.EMPRESTADO)
                .dataDevolucaoPrevista(LocalDate.now().minusDays(1)) // Venceu ontem
                .build();

        when(emprestimoRepository.findByStatus(StatusEmprestimo.EMPRESTADO))
                .thenReturn(List.of(emprestimoVencido));
        
        when(penalidadeRepository.existsByEmprestimoIdAndAtivaTrue(emprestimoVencido.getId()))
                .thenReturn(false);

        // Act
        emprestimoService.verificarAtrasosAutomaticamente();

        // Assert
        assertEquals(StatusEmprestimo.ATRASADO, emprestimoVencido.getStatus());
        verify(penalidadeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Não deve fazer nada se o empréstimo estiver em dia (data no futuro)")
    @SuppressWarnings("java:S8692")
    void testVerificarAtrasosAutomaticamente_EmDia() {
        // Arrange
        Emprestimo emprestimoNoPrazo = Emprestimo.builder()
                .id(UUID.randomUUID())
                .leitor(leitor)
                .status(StatusEmprestimo.EMPRESTADO)
                .dataDevolucaoPrevista(LocalDate.now().plusDays(1)) // Vence amanhã
                .build();

        when(emprestimoRepository.findByStatus(StatusEmprestimo.EMPRESTADO))
                .thenReturn(List.of(emprestimoNoPrazo));

        // Act
        emprestimoService.verificarAtrasosAutomaticamente();

        // Assert
        assertEquals(StatusEmprestimo.EMPRESTADO, emprestimoNoPrazo.getStatus());
        verify(penalidadeRepository, never()).save(any());
    }
}
