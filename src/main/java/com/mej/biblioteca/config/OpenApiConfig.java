package com.mej.biblioteca.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springdoc.core.customizers.OpenApiCustomizer;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";
    private static final String MENSAGEM_KEY = "mensagem";

    @Bean
    public OpenAPI bibliotecaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Biblioteca MEJ API")
                        .description("API para gerenciamento do catalogo, categorias, usuarios, emprestimos, validacao e reenvio de codigo de cadastro por e-mail e alteracao de senha por codigo.")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    @Bean
    public OpenApiCustomizer apiErrorResponsesCustomizer() {
        return openApi -> {
            openApi.getComponents()
                    .addSchemas("FieldErrorResponse", fieldErrorResponseSchema())
                    .addSchemas("ApiErrorResponse", apiErrorResponseSchema());
            
            if (openApi.getPaths() != null) {
                openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                    io.swagger.v3.oas.models.responses.ApiResponses responses = operation.getResponses();
                    if (!responses.containsKey("400")) responses.addApiResponse("400", respostaErro(HttpStatus.BAD_REQUEST));
                    if (!responses.containsKey("403")) responses.addApiResponse("403", respostaErro(HttpStatus.FORBIDDEN));
                    if (!responses.containsKey("404")) responses.addApiResponse("404", respostaErro(HttpStatus.NOT_FOUND));
                    if (!responses.containsKey("500")) responses.addApiResponse("500", respostaErro(HttpStatus.INTERNAL_SERVER_ERROR));
                }));
            }
        };
    }

    private io.swagger.v3.oas.models.responses.ApiResponse respostaErro(HttpStatus status) {
        return new io.swagger.v3.oas.models.responses.ApiResponse()
                .description(status.getReasonPhrase())
                .content(new Content().addMediaType(
                        org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse"))
                ));
    }

    private Schema<?> fieldErrorResponseSchema() {
        return new ObjectSchema()
                .addProperty("campo", new Schema<String>().type("string"))
                .addProperty(MENSAGEM_KEY, new Schema<String>().type("string"));
    }

    private Schema<?> apiErrorResponseSchema() {
        return new ObjectSchema()
                .addProperty("timestamp", new Schema<String>().type("string").format("date-time"))
                .addProperty("status", new IntegerSchema())
                .addProperty("erro", new Schema<String>().type("string"))
                .addProperty(MENSAGEM_KEY, new Schema<String>().type("string"))
                .addProperty("path", new Schema<String>().type("string"))
                .addProperty("campos", new ArraySchema()
                        .items(new Schema<>().$ref("#/components/schemas/FieldErrorResponse")));
    }
}
