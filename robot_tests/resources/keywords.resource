*** Settings ***
Library    RequestsLibrary
Library    String
Resource   api_config.resource

*** Variables ***
${ADMIN_EMAIL}        admin@biblioteca.com
${ADMIN_PASSWORD}     Admin@123p

${LEITOR_EMAIL}       leitor@biblioteca.com
${LEITOR_PASSWORD}    Leitor@123

*** Keywords ***
Gerar Token Admin
    [Documentation]    Faz login com a conta de administrador (criada pelo ambiente) e retorna o token JWT
    ${headers}=    Obter Headers Padrao
    ${body}=       Create Dictionary    identificador=${ADMIN_EMAIL}    senha=${ADMIN_PASSWORD}
    ${response}=   POST On Session    biblioteca_api    /auth/login    json=${body}    headers=${headers}    expected_status=200
    ${token}=      Set Variable    ${response.json()}[token]
    RETURN         ${token}

Gerar Token Leitor
    [Documentation]    Faz login com a conta de leitor de testes (criada pelo DatabaseSeeder) e retorna o token JWT
    ${headers}=    Obter Headers Padrao
    ${body}=       Create Dictionary    identificador=${LEITOR_EMAIL}    senha=${LEITOR_PASSWORD}
    ${response}=   POST On Session    biblioteca_api    /auth/login    json=${body}    headers=${headers}    expected_status=200
    ${token}=      Set Variable    ${response.json()}[token]
    RETURN         ${token}
