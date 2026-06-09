package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.AlterarRoleRequest;
import com.mej.biblioteca.dto.UsuarioResponse;
import com.mej.biblioteca.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public Page<UsuarioResponse> listar(@ParameterObject @PageableDefault(size = 10, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        return usuarioService.listar(pageable);
    }

    @GetMapping("/me")
    @Operation(summary = "Obter o próprio perfil do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Perfil do usuário")
    public UsuarioResponse meuPerfil(Authentication authentication) {
        return UsuarioResponse.from(usuarioService.usuarioAutenticado(authentication));
    }

    @PatchMapping("/{id}/promover-admin")
    @Operation(summary = "Promover usuário para ADMIN")
    @ApiResponse(responseCode = "200", description = "Usuário promovido para ADMIN")
    public UsuarioResponse promoverAdmin(@PathVariable UUID id) {
        return usuarioService.promoverAdmin(id);
    }

    @PatchMapping("/{id}/rebaixar-leitor")
    @Operation(summary = "Rebaixar administrador para LEITOR")
    @ApiResponse(responseCode = "200", description = "Administrador rebaixado para LEITOR")
    public UsuarioResponse rebaixarLeitor(@PathVariable UUID id) {
        return usuarioService.rebaixarLeitor(id);
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Alterar role do usuário")
    @ApiResponse(responseCode = "200", description = "Role alterada")
    public UsuarioResponse alterarRole(@PathVariable UUID id, @RequestBody @Valid AlterarRoleRequest request) {
        return usuarioService.alterarRole(id, request.role());
    }

    @PatchMapping("/{id}/bloquear")
    public UsuarioResponse bloquear(@PathVariable UUID id) {
        return usuarioService.bloquear(id);
    }

    @PatchMapping("/{id}/desbloquear")
    public UsuarioResponse desbloquear(@PathVariable UUID id) {
        return usuarioService.desbloquear(id);
    }
}
