*** Settings ***
Documentation    Suite de testes para validar o UsuarioController
Resource         ../resources/keywords.resource
Library          Collections
Library          String

Suite Setup      Criar Sessao Biblioteca

*** Variables ***
${USUARIOS_ENDPOINT}    /usuarios

*** Test Cases ***
CT01: (ADMIN) Deve listar usuarios com paginacao e status 200
    [Documentation]    Testa a listagem de usuários apenas para admins
    ${token}=      Gerar Token Admin
    ${headers}=    Obter Headers Padrao
    Set To Dictionary    ${headers}    Authorization    Bearer ${token}

    ${response}=    GET On Session    
    ...    biblioteca_api    
    ...    ${USUARIOS_ENDPOINT}    
    ...    headers=${headers}    
    ...    expected_status=200

    Dictionary Should Contain Key    ${response.json()}    content

CT02: (LEITOR) Deve receber 403 ao tentar listar todos os usuarios
    [Documentation]    Testa a segurança da rota de listagem contra acessos de leitores
    ${token}=      Gerar Token Leitor
    ${headers}=    Obter Headers Padrao
    Set To Dictionary    ${headers}    Authorization    Bearer ${token}

    GET On Session    
    ...    biblioteca_api    
    ...    ${USUARIOS_ENDPOINT}    
    ...    headers=${headers}    
    ...    expected_status=403

CT03: (LEITOR/ADMIN) Deve conseguir acessar o proprio perfil em /me
    [Documentation]    Testa o endpoint de visualização do próprio perfil
    ${token}=      Gerar Token Leitor
    ${headers}=    Obter Headers Padrao
    Set To Dictionary    ${headers}    Authorization    Bearer ${token}

    ${response}=    GET On Session    
    ...    biblioteca_api    
    ...    ${USUARIOS_ENDPOINT}/me    
    ...    headers=${headers}    
    ...    expected_status=200

    Should Be Equal As Strings    ${response.json()}[email]    ${LEITOR_EMAIL}
