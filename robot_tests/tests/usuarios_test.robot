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

CT04: (ADMIN) Deve conseguir bloquear um usuario informando o motivo
    [Documentation]    Testa o bloqueio de um usuario passando um request com o motivo
    ${token_leitor}=   Gerar Token Leitor
    ${headers_leitor}= Obter Headers Padrao
    Set To Dictionary  ${headers_leitor}    Authorization    Bearer ${token_leitor}
    
    ${resp_me}=    GET On Session    biblioteca_api    ${USUARIOS_ENDPOINT}/me    headers=${headers_leitor}    expected_status=200
    ${leitor_id}=  Set Variable    ${resp_me.json()}[id]
    Set Global Variable    ${ID_LEITOR_BLOQUEIO}    ${leitor_id}

    ${token_admin}=    Gerar Token Admin
    ${headers_admin}=  Obter Headers Padrao
    Set To Dictionary  ${headers_admin}    Authorization    Bearer ${token_admin}
    
    ${body}=    Create Dictionary    motivoBloqueio=Falta de devolucao
    
    ${resp_bloq}=    PATCH On Session    biblioteca_api    ${USUARIOS_ENDPOINT}/${ID_LEITOR_BLOQUEIO}/bloquear    json=${body}    headers=${headers_admin}    expected_status=200
    
    Should Be True    ${resp_bloq.json()}[loginBloqueado]
    Should Be Equal As Strings    ${resp_bloq.json()}[motivoBloqueio]    Falta de devolucao

CT05: (ADMIN) Deve conseguir desbloquear um usuario e limpar o motivo
    [Documentation]    Testa o desbloqueio de um usuario bloqueado, garantindo a limpeza do motivo
    ${token_admin}=    Gerar Token Admin
    ${headers_admin}=  Obter Headers Padrao
    Set To Dictionary  ${headers_admin}    Authorization    Bearer ${token_admin}
    
    ${resp_desbloq}=   PATCH On Session    biblioteca_api    ${USUARIOS_ENDPOINT}/${ID_LEITOR_BLOQUEIO}/desbloquear    headers=${headers_admin}    expected_status=200
    
    Should Not Be True    ${resp_desbloq.json()}[loginBloqueado]
    Dictionary Should Not Contain Value    ${resp_desbloq.json()}    Falta de devolucao

CT06: (ADMIN) Deve conseguir listar filtrando os usuarios
    [Documentation]    Testa os parametros de busca da listagem (role, ativo, loginBloqueado)
    ${token_admin}=    Gerar Token Admin
    ${headers_admin}=  Obter Headers Padrao
    Set To Dictionary  ${headers_admin}    Authorization    Bearer ${token_admin}
    
    ${response}=    GET On Session    biblioteca_api    ${USUARIOS_ENDPOINT}?role=LEITOR&ativo=true&loginBloqueado=false    headers=${headers_admin}    expected_status=200
    
    Dictionary Should Contain Key    ${response.json()}    content
