*** Settings ***
Documentation    Suite de testes para validar o AuthController (Login e Cadastro)
Resource         ../resources/api_config.resource
Library          Collections
Library          String

Suite Setup      Criar Sessao Biblioteca

*** Variables ***
${CADASTRO_ENDPOINT}    /auth/cadastro
${LOGIN_ENDPOINT}       /auth/login

*** Test Cases ***
CT01: Tentar login com e-mail inexistente deve retornar 401
    [Documentation]    Verifica se a API retorna erro 401 (Unauthorized) quando um usuário não cadastrado tenta fazer login.
    ${headers}=    Obter Headers Padrao
    ${body}=       Create Dictionary    identificador=naoexiste@gmail.com    senha=Qualquer123

    ${response}=    POST On Session    
    ...    biblioteca_api    
    ...    ${LOGIN_ENDPOINT}    
    ...    json=${body}    
    ...    headers=${headers}    
    ...    expected_status=401

    Dictionary Should Contain Key    ${response.json()}    mensagem
    Should Be Equal As Strings       ${response.json()}[mensagem]    Credenciais inválidas.

CT02: Tentar cadastrar um usuario com dados invalidos (senha fraca) deve retornar 400
    [Documentation]    Verifica as validacoes do DTO do cadastro (senha muito fraca)
    ${headers}=    Obter Headers Padrao
    ${body}=       Create Dictionary    
    ...    nomeCompleto=Joao Teste    
    ...    email=joaoteste@gmail.com    
    ...    telefoneWhatsapp=85999999999    
    ...    senha=fraca

    ${response}=    POST On Session    
    ...    biblioteca_api    
    ...    ${CADASTRO_ENDPOINT}    
    ...    json=${body}    
    ...    headers=${headers}    
    ...    expected_status=400

    Dictionary Should Contain Key    ${response.json()}    campos

CT03: Cadastrar um usuario com sucesso deve retornar 201 Created
    [Documentation]    Verifica se o fluxo feliz de cadastro retorna o Status HTTP 201 e o ID do usuário.
    # Gera um email e telefone aleatórios para não dar erro de duplicidade
    ${random_str}=    Generate Random String    6    [LOWER]
    ${random_num}=    Generate Random String    8    [NUMBERS]
    ${email}=         Set Variable    joao_${random_str}@gmail.com
    ${telefone}=      Set Variable    859${random_num}

    ${headers}=    Obter Headers Padrao
    ${body}=       Create Dictionary    
    ...    nomeCompleto=Joao Teste da Silva    
    ...    email=${email}    
    ...    telefoneWhatsapp=${telefone}    
    ...    senha=Teste@123

    ${response}=    POST On Session    
    ...    biblioteca_api    
    ...    ${CADASTRO_ENDPOINT}    
    ...    json=${body}    
    ...    headers=${headers}    
    ...    expected_status=201

    Dictionary Should Contain Key    ${response.json()}    id
    Set Global Variable    ${NOVO_USUARIO_ID}    ${response.json()}[id]
    Log    Usuário cadastrado com sucesso! ID: ${NOVO_USUARIO_ID}
