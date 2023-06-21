package br.com.wallace.email.dtos;

import br.com.wallace.email.models.EmailModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(@NotBlank String ownerRef, @NotBlank @Email String emailFrom, @NotBlank @Email String emailTo, @NotBlank String subject, @NotBlank String text) {
    public EmailRequest(EmailModel email) {
        this(email.getOwnerRef(), email.getEmailFrom() , email.getEmailTo(), email.getSubject(), email.getText() );
    }
}
