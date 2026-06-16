*** Settings ***
Documentation    Suite de testes para validar o LivroController
Resource         ../resources/keywords.resource
Library          Collections
Library          String

Suite Setup      Criar Sessao Biblioteca

*** Variables ***
${LIVROS_ENDPOINT}    /livros

*** Test Cases ***
CT01: (ADMIN) Deve criar um livro novo retornando 201
    [Documentation]    Cria um livro novo passando o JWT de Admin
    ${token}=      Gerar Token Admin
    ${headers}=    Obter Headers Padrao
    Set To Dictionary    ${headers}    Authorization    Bearer ${token}

    ${random_str}=    Generate Random String    4    [UPPER]
    ${lista_vazia}=    Create List
    ${body}=       Create Dictionary    
    ...    nomeObra=Livro de Testes ${random_str}
    ...    autor=Autor Ficticio
    ...    editora=Editora Robot
    ...    volume=1
    ...    descricao=Livro para testes automatizados
    ...    categoriasIds=${lista_vazia}
    ...    quantidade=5
    ...    fotoCapaUrl=https://res.cloudinary.com/teste/image.webp

    ${response}=    POST On Session    
    ...    biblioteca_api    
    ...    ${LIVROS_ENDPOINT}    
    ...    json=${body}    
    ...    headers=${headers}    
    ...    expected_status=201

    Dictionary Should Contain Key    ${response.json()}    id
    Set Global Variable    ${NOVO_LIVRO_ID}    ${response.json()}[id]

CT02: (LEITOR) Deve receber Forbidden 403 ao tentar criar um livro
    [Documentation]    Tenta criar um livro com Token de Leitor
    ${token}=      Gerar Token Leitor
    ${headers}=    Obter Headers Padrao
    Set To Dictionary    ${headers}    Authorization    Bearer ${token}

    ${body}=       Create Dictionary    nomeObra=Hacker Book    autor=Anon    editora=Null    volume=1    quantidade=1    fotoCapaUrl=https://res.cloudinary.com/img.webp
    
    POST On Session    
    ...    biblioteca_api    
    ...    ${LIVROS_ENDPOINT}    
    ...    json=${body}    
    ...    headers=${headers}    
    ...    expected_status=403

CT03: Validacao DTO deve falhar com 400 se faltar nomeObra
    [Documentation]    Testa a validação de campos vazios (Status 400 Bad Request)
    ${token}=      Gerar Token Admin
    ${headers}=    Obter Headers Padrao
    Set To Dictionary    ${headers}    Authorization    Bearer ${token}

    ${body}=       Create Dictionary    nomeObra=${EMPTY}    autor=Anon    editora=Null    volume=1    quantidade=1    fotoCapaUrl=https://res.cloudinary.com/img.webp
    
    POST On Session    
    ...    biblioteca_api    
    ...    ${LIVROS_ENDPOINT}    
    ...    json=${body}    
    ...    headers=${headers}    
    ...    expected_status=400

CT04: GET /livros deve listar livros paginados com 200 OK
    [Documentation]    Lista os livros e verifica a estrutura da paginação
    ${headers}=    Obter Headers Padrao
    
    ${response}=    GET On Session    
    ...    biblioteca_api    
    ...    ${LIVROS_ENDPOINT}    
    ...    headers=${headers}    
    ...    expected_status=200

    Dictionary Should Contain Key    ${response.json()}    content
    Dictionary Should Contain Key    ${response.json()}    totalElements

CT05: GET /livros/{id} deve retornar 404 para ID inexistente
    [Documentation]    Busca um UUID que não existe
    ${headers}=    Obter Headers Padrao
    
    GET On Session    
    ...    biblioteca_api    
    ...    ${LIVROS_ENDPOINT}/00000000-0000-0000-0000-000000000000
    ...    headers=${headers}    
    ...    expected_status=404
