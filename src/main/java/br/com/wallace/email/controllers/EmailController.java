package br.com.wallace.email.controllers;

import br.com.wallace.email.dtos.EmailRequest;
import br.com.wallace.email.dtos.EmailResponse;
import br.com.wallace.email.models.EmailModel;
import br.com.wallace.email.services.EmailService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sending-email")
    public ResponseEntity<EmailRequest> sendingEmail(@RequestBody @Valid EmailRequest emailRequest){
        var email = new EmailModel(emailRequest);
        emailService.sendEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EmailRequest(email));
    }

    @GetMapping("/emails")
    @JsonIgnore
    public ResponseEntity<List<EmailResponse>> getAllEmails(@PageableDefault(sort = "emailId") Pageable page){
        Page<EmailModel> placePage = emailService.findAll(page);
        List<EmailResponse> emails = placePage.getContent().stream().map(EmailResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(emails);
    }

}

