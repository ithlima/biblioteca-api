package com.mej.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConfirmarAlteracaoSenhaRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^\\d{6}$", message = "O codigo deve conter 6 digitos.") String codigo,
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$",
                message = "A senha deve ter no minimo 8 caracteres, com letra maiuscula, letra minuscula, numero e simbolo."
        )
        String novaSenha
) {
}
