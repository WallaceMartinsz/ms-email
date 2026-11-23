package br.com.wallace.email.messaging;

import br.com.wallace.email.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${app.messaging.queue}")
    public void consume(EmailMessage message) {
        try {
            emailService.process(message.emailId());
        } catch (RuntimeException e) {
            log.warn("E-mail {} falhou na tentativa: {}", message.emailId(), e.getMessage());
            throw e;
        }
    }
}
