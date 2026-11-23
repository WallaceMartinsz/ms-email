package br.com.wallace.email.controllers;

import br.com.wallace.email.dtos.EmailRequest;
import br.com.wallace.email.dtos.EmailResponse;
import br.com.wallace.email.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "E-mails")
@ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
@ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "Enfileira um e-mail para envio", description = "Requer papel ADM. O envio é assíncrono: acompanhe o status pelo GET /emails/{id}.")
    @ApiResponse(responseCode = "202", description = "Aceito, status PENDING")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @PostMapping
    public ResponseEntity<EmailResponse> send(@RequestBody @Valid EmailRequest request) {
        var email = emailService.enqueue(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(email.getEmailId()).toUri();
        return ResponseEntity.accepted().location(location).body(new EmailResponse(email));
    }

    @Operation(summary = "Lista os e-mails", description = "Requer papel USER ou ADM.")
    @ApiResponse(responseCode = "200", description = "Página de e-mails")
    @GetMapping
    public Page<EmailResponse> list(@ParameterObject @PageableDefault(sort = "emailId", direction = Sort.Direction.DESC) Pageable page) {
        return emailService.findAll(page).map(EmailResponse::new);
    }

    @Operation(summary = "Consulta o status de um e-mail", description = "Requer papel USER ou ADM.")
    @ApiResponse(responseCode = "200", description = "E-mail encontrado")
    @ApiResponse(responseCode = "404", description = "E-mail não encontrado", content = @Content)
    @GetMapping("/{id}")
    public EmailResponse get(@PathVariable Long id) {
        return new EmailResponse(emailService.findById(id));
    }
}
