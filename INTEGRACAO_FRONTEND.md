# Guia de Integração - Backend Biblioteca (Atualizações Recentes)

Olá, equipe de Frontend! 👋

Este documento resume as recentes melhorias aplicadas no backend (Spring Boot) do sistema da Biblioteca e detalha o que vocês precisam ajustar no lado do cliente para consumir a API corretamente.

---

## 1. 📄 Nova Estrutura de Resposta: Paginação

Para garantir a alta performance da API e evitar sobrecarga de memória conforme a base de dados cresce, implementamos paginação em **todas as rotas de listagem**.

Isso afeta principalmente os seguintes endpoints (e quaisquer outros que retornavam listas/arrays):
- `GET /livros`
- `GET /usuarios`
- `GET /emprestimos`
- `GET /emprestimos/meus`

### ❌ Como era antes (Array Direto):
Antes a API retornava um array direto com os resultados.
```json
[
  { "id": "1", "nomeObra": "Harry Potter e a Pedra Filosofal" },
  { "id": "2", "nomeObra": "Senhor dos Anéis" }
]
```
*(No frontend, vocês provavelmente liam o array em `response.data`)*

### ✅ Como está agora (Objeto Paginado do Spring):
A API agora retorna um objeto JSON que contém metadados sobre a página, e a **lista real de resultados passa a vir dentro da propriedade `"content"`**.
```json
{
  "content": [
    { "id": "1", "nomeObra": "Harry Potter e a Pedra Filosofal" },
    { "id": "2", "nomeObra": "Senhor dos Anéis" }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": { "empty": true, "sorted": false, "unsorted": true },
    "offset": 0,
    "unpaged": false,
    "paged": true
  },
  "last": false,
  "totalElements": 45,
  "totalPages": 5,
  "size": 10,
  "number": 0,
  "first": true,
  "numberOfElements": 10,
  "empty": false
}
```

### 🛠️ O que vocês precisam alterar no código:
1. **Extrair os dados corretamente:** Atualizem as requisições para ler o array de resultados em `response.data.content` em vez de `response.data`.
2. **Navegação (Paginação):** Para pedir as próximas páginas, vocês devem enviar parâmetros na URL da requisição. O padrão do Spring é *zero-indexed* (Página 1 é `0`).
   - Exemplo: `GET /livros?page=0&size=10` (Busca os primeiros 10 livros)
   - Exemplo: `GET /livros?page=1&size=10` (Busca do 11º ao 20º livro)
3. **Controles de UI:** Usem as propriedades de retorno `totalElements` (total de itens na base) e `totalPages` (total de páginas disponíveis) para renderizar a barra de paginação no layout da aplicação (Botões: Anterior, 1, 2, 3, Próximo).

---

## 2. 🌐 Política de CORS e Deploy

A configuração de CORS (Cross-Origin Resource Sharing) do backend não permite mais qualquer URL em ambiente de produção para garantir segurança, e deixou de estar fixada (hardcoded) no código.

**Impacto:**
- **Em desenvolvimento local (`localhost`):** As portas padrões do React/Vite/Angular (`http://localhost:5173`, `http://localhost:3000`, `http://localhost:4200`) continuam liberadas por padrão no arquivo `application.properties` para não atrapalhar o desenvolvimento de vocês. 
- **Em Produção:** Quando vocês fizerem o deploy do Frontend em uma URL pública (ex: `https://meu-front.com.br`), o time de Backend / DevOps vai precisar adicionar essa URL na variável de ambiente `CORS_ALLOWED_ORIGINS` no servidor do backend.

Se ao realizar algum teste no servidor vocês receberem erro de "CORS Policy", peçam imediatamente para o responsável pelo backend incluir a origem do frontend nas variáveis de servidor!

---
Qualquer dúvida técnica ou erro retornado em rotas não documentadas, verifiquem também a documentação do Swagger acessando `/swagger-ui.html` rodando o backend localmente!
