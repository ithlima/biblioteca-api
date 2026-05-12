package com.mej.biblioteca;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mej.biblioteca.model.CodigoVerificacao;
import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.TipoCodigoVerificacao;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.CodigoVerificacaoRepository;
import com.mej.biblioteca.repository.EmprestimoRepository;
import com.mej.biblioteca.repository.LivroRepository;
import com.mej.biblioteca.repository.PenalidadeRepository;
import com.mej.biblioteca.repository.UsuarioRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class RotasPrincipaisIntegrationTests {

    private static final String SENHA = "Senha@123";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private PenalidadeRepository penalidadeRepository;

    @Autowired
    private CodigoVerificacaoRepository codigoVerificacaoRepository;

    @BeforeEach
    void setUp() {
        codigoVerificacaoRepository.deleteAll();
        penalidadeRepository.deleteAll();
        emprestimoRepository.deleteAll();
        livroRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void rotasPrincipaisRespondemComStatusEsperado() throws Exception {
        Usuario admin = criarUsuario("Admin MEJ", "admin@mej.com", "85911110000", Role.ADMIN, true, false);
        Usuario leitor = criarUsuario("Leitor MEJ", "leitor@mej.com", "85922220000", Role.LEITOR, true, false);
        Usuario outroLeitor = criarUsuario("Outro Leitor", "outro@mej.com", "85933330000", Role.LEITOR, true, false);

        String adminToken = login("admin@mej.com", SENHA);
        String leitorToken = login("leitor@mej.com", SENHA);

        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk());

        String livroId = criarLivro(adminToken);

        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeObra").value("Livro de Teste"));

        mockMvc.perform(get("/livros/{id}", livroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(livroId));

        mockMvc.perform(put("/livros/{id}", livroId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeObra": "Livro de Teste Atualizado",
                                  "autor": "Autor Teste",
                                  "editora": "Editora MEJ",
                                  "volume": "1",
                                  "descricao": "Descricao atualizada",
                                  "categorias": "catequese",
                                  "quantidade": 2,
                                  "fotoCapaUrl": "https://exemplo.com/capa.jpg"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeObra").value("Livro de Teste Atualizado"));

        String emprestimoId = solicitarEmprestimo(leitorToken, livroId);

        mockMvc.perform(get("/emprestimos/meus")
                        .header(HttpHeaders.AUTHORIZATION, bearer(leitorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(emprestimoId));

        mockMvc.perform(get("/emprestimos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(emprestimoId));

        mockMvc.perform(post("/emprestimos/{id}/emprestar", emprestimoId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EMPRESTADO"));

        mockMvc.perform(patch("/emprestimos/{id}/renovar", emprestimoId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(leitorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeRenovacoes").value(1));

        mockMvc.perform(get("/penalidades/minhas")
                        .header(HttpHeaders.AUTHORIZATION, bearer(leitorToken)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/penalidades")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/penalidades/verificar-atrasos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        mockMvc.perform(patch("/emprestimos/{id}/devolver", emprestimoId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(leitorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEVOLVIDO"));

        mockMvc.perform(patch("/livros/{id}/ocultar", livroId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motivoOcultacao": "Teste de rota"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oculto").value(true));

        mockMvc.perform(patch("/livros/{id}/disponibilizar", livroId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oculto").value(false));

        mockMvc.perform(get("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/usuarios/{id}/promover-admin", outroLeitor.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        mockMvc.perform(patch("/usuarios/{id}/rebaixar-leitor", outroLeitor.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("LEITOR"));

        mockMvc.perform(patch("/usuarios/{id}/bloquear", outroLeitor.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginBloqueado").value(true));

        mockMvc.perform(delete("/livros/{id}", livroId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nao e permitido remover livro com historico de emprestimos."));

        String livroSemHistoricoId = criarLivroParaRemocao(adminToken);

        mockMvc.perform(delete("/livros/{id}", livroSemHistoricoId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isNoContent());

        testarRotasAuthPublicas();

        assertThat(usuarioRepository.findById(admin.getId())).isPresent();
        assertThat(usuarioRepository.findById(leitor.getId())).isPresent();
    }

    @Test
    void rotasProtegidasRecusamUsuarioSemPermissao() throws Exception {
        criarUsuario("Leitor MEJ", "leitor@mej.com", "85922220000", Role.LEITOR, true, false);
        String leitorToken = login("leitor@mej.com", SENHA);

        mockMvc.perform(post("/livros")
                        .header(HttpHeaders.AUTHORIZATION, bearer(leitorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeObra": "Livro Negado",
                                  "autor": "Autor",
                                  "quantidade": 1
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, bearer(leitorToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/emprestimos"))
                .andExpect(status().isForbidden());
    }

    private void testarRotasAuthPublicas() throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeCompleto": "Cadastro Rota",
                                  "email": "cadastro@mej.com",
                                  "telefoneWhatsapp": "85944440000",
                                  "senha": "Senha@123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("cadastro@mej.com"));

        criarCodigo("cadastro@mej.com", TipoCodigoVerificacao.CADASTRO, "123456");

        mockMvc.perform(post("/auth/cadastro/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "cadastro@mej.com",
                                  "codigo": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        mockMvc.perform(post("/auth/senha/solicitar-alteracao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "cadastro@mej.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        criarCodigo("cadastro@mej.com", TipoCodigoVerificacao.ALTERACAO_SENHA, "654321");

        mockMvc.perform(post("/auth/senha/confirmar-alteracao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "cadastro@mej.com",
                                  "codigo": "654321",
                                  "novaSenha": "Nova@123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identificador": "cadastro@mej.com",
                                  "senha": "Nova@123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    private String criarLivro(String adminToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/livros")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeObra": "Livro de Teste",
                                  "autor": "Autor Teste",
                                  "editora": "Editora MEJ",
                                  "volume": "1",
                                  "descricao": "Descricao",
                                  "categorias": "catequese",
                                  "quantidade": 2,
                                  "fotoCapaUrl": "https://exemplo.com/capa.jpg"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        return json(result).get("id").asText();
    }

    private String criarLivroParaRemocao(String adminToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/livros")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeObra": "Livro Sem Historico",
                                  "autor": "Autor Teste",
                                  "editora": "Editora MEJ",
                                  "volume": "2",
                                  "descricao": "Descricao",
                                  "categorias": "catequese",
                                  "quantidade": 1,
                                  "fotoCapaUrl": "https://exemplo.com/capa.jpg"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        return json(result).get("id").asText();
    }

    private String solicitarEmprestimo(String leitorToken, String livroId) throws Exception {
        MvcResult result = mockMvc.perform(post("/emprestimos/solicitar")
                        .header(HttpHeaders.AUTHORIZATION, bearer(leitorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "livroId": "%s"
                                }
                                """.formatted(livroId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SOLICITADO"))
                .andReturn();

        return json(result).get("id").asText();
    }

    private String login(String identificador, String senha) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identificador": "%s",
                                  "senha": "%s"
                                }
                                """.formatted(identificador, senha)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        return json(result).get("token").asText();
    }

    private Usuario criarUsuario(String nome, String email, String telefone, Role role, boolean ativo, boolean bloqueado) {
        return usuarioRepository.save(Usuario.builder()
                .nomeCompleto(nome)
                .email(email)
                .telefoneWhatsapp(telefone)
                .senha(passwordEncoder.encode(SENHA))
                .role(role)
                .ativo(ativo)
                .loginBloqueado(bloqueado)
                .emailValidado(true)
                .build());
    }

    private void criarCodigo(String email, TipoCodigoVerificacao tipo, String codigo) {
        codigoVerificacaoRepository.save(CodigoVerificacao.builder()
                .email(email)
                .codigoHash(passwordEncoder.encode(codigo))
                .tipo(tipo)
                .expiraEm(LocalDateTime.now().plusMinutes(5))
                .build());
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
