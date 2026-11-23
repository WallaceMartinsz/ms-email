package br.com.wallace.email.services;

import br.com.wallace.email.dtos.EmailRequest;
import br.com.wallace.email.enums.StatusEmail;
import br.com.wallace.email.exceptions.EmailNotFoundException;
import br.com.wallace.email.messaging.EmailMessage;
import br.com.wallace.email.models.EmailModel;
import br.com.wallace.email.repositories.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final EmailSender emailSender;
    private final ApplicationEventPublisher events;

    @Transactional
    public EmailModel enqueue(EmailRequest request) {
        var email = emailRepository.save(EmailModel.pendingFrom(request));
        log.info("E-mail {} criado (PENDING) para {}", email.getEmailId(), email.getEmailTo());
        events.publishEvent(new EmailMessage(email.getEmailId()));
        return email;
    }

    @Transactional
    public void process(Long emailId) {
        var email = emailRepository.findById(emailId).orElse(null);
        if (email == null) {
            log.warn("E-mail {} não existe mais, mensagem descartada", emailId);
            return;
        }
        if (email.getStatusEmail() == StatusEmail.SENT) {
            log.info("E-mail {} já enviado, ignorando mensagem duplicada", emailId);
            return; // idempotência: o RabbitMQ entrega "at least once"
        }
        emailSender.send(email);
        email.markAsSent();
    }

    @Transactional
    public void markAsError(Long emailId, String reason) {
        emailRepository.findById(emailId).ifPresent(e -> e.markAsError(reason));
    }

    @Transactional(readOnly = true)
    public Page<EmailModel> findAll(Pageable pageable) {
        return emailRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public EmailModel findById(Long id) {
        return emailRepository.findById(id).orElseThrow(() -> new EmailNotFoundException(id));
    }
}
