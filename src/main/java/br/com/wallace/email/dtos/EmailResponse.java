package br.com.wallace.email.dtos;

import br.com.wallace.email.enums.StatusEmail;
import br.com.wallace.email.models.EmailModel;

import java.time.LocalDateTime;

public record EmailResponse(Long emailId, String ownerRef, String emailFrom, String emailTo, String subject,
                            String text, StatusEmail status, String errorMessage,
                            LocalDateTime createdAt, LocalDateTime sendDateEmail) {

    public EmailResponse(EmailModel email) {
        this(email.getEmailId(), email.getOwnerRef(), email.getEmailFrom(), email.getEmailTo(), email.getSubject(),
                email.getText(), email.getStatusEmail(), email.getErrorMessage(),
                email.getCreatedAt(), email.getSendDateEmail());
    }
}
