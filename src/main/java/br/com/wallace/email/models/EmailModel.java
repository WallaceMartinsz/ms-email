package br.com.wallace.email.models;

import br.com.wallace.email.dtos.EmailRequest;
import br.com.wallace.email.enums.StatusEmail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_email")
@Getter
@Setter
@NoArgsConstructor
public class EmailModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailId;
    private String ownerRef;
    private String emailFrom;
    private String emailTo;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String text;
    @Enumerated(EnumType.STRING)
    private StatusEmail statusEmail;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime sendDateEmail;

    public static EmailModel pendingFrom(EmailRequest request) {
        var email = new EmailModel();
        email.ownerRef = request.ownerRef();
        email.emailFrom = request.emailFrom();
        email.emailTo = request.emailTo();
        email.subject = request.subject();
        email.text = request.text();
        email.statusEmail = StatusEmail.PENDING;
        email.createdAt = LocalDateTime.now();
        return email;
    }

    public void markAsSent() {
        this.statusEmail = StatusEmail.SENT;
        this.sendDateEmail = LocalDateTime.now();
        this.errorMessage = null;
    }

    public void markAsError(String reason) {
        this.statusEmail = StatusEmail.ERROR;
        this.errorMessage = reason;
    }
}
