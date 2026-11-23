package br.com.wallace.email.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailRequest(
        @Schema(example = "pedido-123") @NotBlank String ownerRef,
        @Schema(example = "no-reply@ms-email.local") @NotBlank @Email String emailFrom,
        @Schema(example = "cliente@exemplo.com") @NotBlank @Email String emailTo,
        @Schema(example = "Pedido confirmado") @NotBlank @Size(max = 255) String subject,
        @Schema(example = "Seu pedido foi confirmado!") @NotBlank String text) {
}
