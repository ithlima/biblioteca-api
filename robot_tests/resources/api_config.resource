*** Settings ***
Library    RequestsLibrary

*** Variables ***
${BASE_URL}    http://localhost:8080
${CONTENT_TYPE}    application/json

*** Keywords ***
Criar Sessao Biblioteca
    Create Session    biblioteca_api    ${BASE_URL}    verify=True

Obter Headers Padrao
    ${headers}=    Create Dictionary    Content-Type=${CONTENT_TYPE}
    RETURN    ${headers}
