package br.com.wallace.email.models;

import br.com.wallace.email.dtos.EmailRequest;
import br.com.wallace.email.enums.StatusEmail;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "tb_email")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailModel implements Serializable {
    private static final Long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long emailId;
    private String ownerRef;
    private String emailFrom;
    private String emailTo;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String text;
    private LocalDateTime sendDateEmail;
    private StatusEmail statusEmail;

    public EmailModel(EmailRequest emailRequest) {
        this.emailFrom = emailRequest.emailFrom();
        this.ownerRef = emailRequest.ownerRef();
        this.emailTo = emailRequest.emailTo();
        this.subject = emailRequest.subject();
        this.text = emailRequest.text();
    }

}