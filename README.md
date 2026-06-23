# 📚 Biblioteca API

Esta é a API do sistema de biblioteca online do Movimento Eucarístico Jovem (MEJ). 
Foi desenvolvida em **Java** com o framework **Spring Boot** e utiliza banco de dados relacional.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3** (Actuator, Data JPA, Security, Validation, Web, Mail)
- **MySQL / MariaDB** (Banco de Dados)
- **Flyway** (Migrations do Banco)
- **Lombok** (Produtividade)
- **JWT (JSON Web Token)** (Autenticação)
- **Swagger / OpenAPI** (Documentação da API)
- **Jacoco & SonarQube** (Qualidade de Código)

## 📋 Pré-requisitos

Antes de iniciar, certifique-se de ter instalado em sua máquina:
- [Java 21](https://jdk.java.net/21/)
- Um servidor de banco de dados rodando na porta `3306`. Recomendamos **MariaDB** ou **MySQL**, que podem ser instalados de forma independente ou via pacotes como [XAMPP](https://www.apachefriends.org/pt_br/index.html).
- (Opcional) [Docker](https://www.docker.com/) se preferir subir os serviços via container.

---

## ⚙️ Configuração do Ambiente

1. **Clone o repositório:**
   ```bash
   git clone <URL_DO_REPOSITORIO>
   cd biblioteca-api
   ```

2. **Configure as Variáveis de Ambiente:**
   - O projeto possui um arquivo de exemplo. Faça uma cópia do arquivo `.env.example` e renomeie para `.env` na raiz do projeto.
   - Preencha o arquivo `.env` com as suas credenciais. Por exemplo:
     ```env
     DATABASE_PASSWORD=root
     DATABASE_USERNAME=root
     DATABASE_URL=jdbc:mysql://localhost:3306/biblioteca?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
     
     # Configure também as outras variáveis (JWT_SECRET, senhas do admin e credenciais de e-mail)
     ```

3. **Banco de Dados:**
   - O projeto utiliza o **Flyway**, o que significa que **todas as tabelas serão criadas automaticamente** assim que a aplicação iniciar.
   - Certifique-se apenas de que seu serviço MySQL ou MariaDB está rodando localmente e que o usuário/senha no `.env` estão corretos. 
   - A configuração `createDatabaseIfNotExist=true` na URL do `.env` faz com que até o esquema do banco (`biblioteca`) seja criado automaticamente, caso ainda não exista.

---

## 🏃‍♂️ Rodando a Aplicação

Você pode rodar a aplicação via linha de comando usando o Maven Wrapper (`mvnw`), que já vem incluso no projeto (não precisa ter o Maven instalado globalmente).

**No Windows:**
```cmd
.\mvnw.cmd spring-boot:run
```

**No Linux/Mac:**
```bash
./mvnw spring-boot:run
```

A aplicação iniciará na porta padrão `8080`.

---

## 📖 Documentação da API (Swagger)

A API possui uma interface visual e interativa gerada pelo Swagger (OpenAPI) onde você pode entender o que cada rota faz e testá-las diretamente pelo navegador.

Após iniciar a aplicação com sucesso, acesse:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

> **Dica de Teste:** Alguns endpoints exigem autenticação. Para testá-los, primeiro chame a rota de login para obter o seu Token JWT. Depois, clique no botão **"Authorize"** no topo da página do Swagger, insira o token recebido e libere o acesso aos demais recursos!

---

## 🐳 Docker (Opcional)

Se você preferir rodar a infraestrutura de banco de dados e qualidade de código via Docker, o projeto inclui um arquivo `docker-compose.yml` pré-configurado para subir o MySQL e o SonarQube.

Para iniciar os containers:
```bash
docker-compose up -d
```
*(Nota: Certifique-se de que a senha do MySQL definida no `docker-compose.yml` coincida com a que você colocar no seu arquivo `.env` para que a API consiga se conectar).*
