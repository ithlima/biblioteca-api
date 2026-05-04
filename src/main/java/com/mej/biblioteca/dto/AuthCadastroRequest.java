package com.mej.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthCadastroRequest(
        @NotBlank String nomeCompleto,
        @NotBlank @Email String email,
        String telefoneWhatsapp,
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$",
                message = "A senha deve ter no minimo 8 caracteres, com letra maiuscula, letra minuscula, numero e simbolo."
        )
        String senha
) {
}
