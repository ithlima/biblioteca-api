*** Settings ***
Documentation    Suite de testes para validar o EmprestimoController
Resource         ../resources/keywords.resource
Library          Collections
Library          String

Suite Setup      Criar Sessao Biblioteca

*** Variables ***
${EMPRESTIMOS_ENDPOINT}    /emprestimos

*** Test Cases ***
CT01: (LEITOR) Solicitar emprestimo deve retornar 201 e criar com status SOLICITADO
    [Documentation]    Testa a criação do empréstimo validando a regra do estoque e status
    ${token_admin}=    Gerar Token Admin
    ${headers_admin}=  Obter Headers Padrao
    Set To Dictionary  ${headers_admin}    Authorization    Bearer ${token_admin}

    # Pré-requisito: Admin cria um livro exclusivo para esse teste
    ${lista_vazia}=   Create List
    ${random_str}=    Generate Random String    4    [UPPER]
    ${body_livro}=    Create Dictionary    nomeObra=Livro Emprestimo ${random_str}    autor=Autor Ficticio    editora=Editora Robot    volume=1    descricao=Livro de Teste    categoriasIds=${lista_vazia}    quantidade=1    fotoCapaUrl=https://res.cloudinary.com/teste/image.webp
    ${res_livro}=     POST On Session    biblioteca_api    /livros    json=${body_livro}    headers=${headers_admin}    expected_status=201
    ${livro_id}=      Set Variable    ${res_livro.json()}[id]

    # Leitor solicita o empréstimo
    ${token_leitor}=      Gerar Token Leitor
    ${headers_leitor}=    Obter Headers Padrao
    Set To Dictionary     ${headers_leitor}    Authorization    Bearer ${token_leitor}

    ${body_req}=       Create Dictionary    livroId=${livro_id}
    ${res_emprestimo}=    POST On Session    biblioteca_api    ${EMPRESTIMOS_ENDPOINT}/solicitar    json=${body_req}    headers=${headers_leitor}    expected_status=201

    Dictionary Should Contain Key    ${res_emprestimo.json()}    id
    Should Be Equal As Strings       ${res_emprestimo.json()}[status]    SOLICITADO
    Set Global Variable              ${EMPRESTIMO_ID}    ${res_emprestimo.json()}[id]

CT02: (ADMIN) Emprestar livro deve retornar 200, diminuir quantidade e mudar status para EMPRESTADO
    [Documentation]    Testa a aprovação do empréstimo
    ${token_admin}=    Gerar Token Admin
    ${headers_admin}=  Obter Headers Padrao
    Set To Dictionary  ${headers_admin}    Authorization    Bearer ${token_admin}

    ${res_emprestar}=    POST On Session    biblioteca_api    ${EMPRESTIMOS_ENDPOINT}/${EMPRESTIMO_ID}/emprestar    headers=${headers_admin}    expected_status=200
    Should Be Equal As Strings    ${res_emprestar.json()}[status]    EMPRESTADO

CT03: (LEITOR/ADMIN) Renovar livro deve aumentar a data de devolucao
    [Documentation]    Testa a renovação do empréstimo em andamento
    ${token_leitor}=      Gerar Token Leitor
    ${headers_leitor}=    Obter Headers Padrao
    Set To Dictionary     ${headers_leitor}    Authorization    Bearer ${token_leitor}

    ${res_renovar}=    PATCH On Session    biblioteca_api    ${EMPRESTIMOS_ENDPOINT}/${EMPRESTIMO_ID}/renovar    headers=${headers_leitor}    expected_status=200
    Should Be Equal As Integers    ${res_renovar.json()}[quantidadeRenovacoes]    1

CT04: (LEITOR/ADMIN) Devolver livro deve retornar 200 e mudar status para DEVOLVIDO
    [Documentation]    Testa a devolução do empréstimo
    ${token_leitor}=      Gerar Token Leitor
    ${headers_leitor}=    Obter Headers Padrao
    Set To Dictionary     ${headers_leitor}    Authorization    Bearer ${token_leitor}

    ${res_devolver}=    PATCH On Session    biblioteca_api    ${EMPRESTIMOS_ENDPOINT}/${EMPRESTIMO_ID}/devolver    headers=${headers_leitor}    expected_status=200
    Should Be Equal As Strings    ${res_devolver.json()}[status]    DEVOLVIDO

CT05: Solicitar emprestimo de livro sem estoque deve retornar 400 Bad Request
    [Documentation]    Testa regra de negócio: Livro sem estoque disponível
    ${token_admin}=    Gerar Token Admin
    ${headers_admin}=  Obter Headers Padrao
    Set To Dictionary  ${headers_admin}    Authorization    Bearer ${token_admin}

    # Pré-requisito: Livro com quantidade 0
    ${lista_vazia}=   Create List
    ${random_str}=    Generate Random String    4    [UPPER]
    ${body_livro}=    Create Dictionary    nomeObra=Livro Sem Estoque ${random_str}    autor=Autor    editora=Editora    volume=1    descricao=Descricao    categoriasIds=${lista_vazia}    quantidade=0    fotoCapaUrl=https://res.cloudinary.com/teste/image.webp
    ${res_livro}=     POST On Session    biblioteca_api    /livros    json=${body_livro}    headers=${headers_admin}    expected_status=201
    ${livro_id_zero}=    Set Variable    ${res_livro.json()}[id]

    ${token_leitor}=      Gerar Token Leitor
    ${headers_leitor}=    Obter Headers Padrao
    Set To Dictionary     ${headers_leitor}    Authorization    Bearer ${token_leitor}

    ${body_req}=       Create Dictionary    livroId=${livro_id_zero}
    POST On Session    biblioteca_api    ${EMPRESTIMOS_ENDPOINT}/solicitar    json=${body_req}    headers=${headers_leitor}    expected_status=400
