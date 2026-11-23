package br.com.wallace.email.messaging;

import br.com.wallace.email.services.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailRecoverer implements MessageRecoverer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final RejectAndDontRequeueRecoverer reject = new RejectAndDontRequeueRecoverer();

    @Override
    public void recover(Message message, Throwable cause) {
        Throwable root = rootCause(cause);
        try {
            var payload = objectMapper.readValue(message.getBody(), EmailMessage.class);
            emailService.markAsError(payload.emailId(), root.getMessage());
            log.error("E-mail {} -> ERROR, enviado para a DLQ: {}", payload.emailId(), root.getMessage());
        } catch (Exception e) {
            log.error("Não foi possível registrar a falha da mensagem", e);
        }
        reject.recover(message, cause);
    }

    private static Throwable rootCause(Throwable t) {
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t;
    }
}
