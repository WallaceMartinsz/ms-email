package br.com.wallace.email.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

// Publica só após o commit, para o consumidor nunca receber um id que ainda não existe no banco.
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties props;

    @TransactionalEventListener
    public void onEmailCreated(EmailMessage message) {
        rabbitTemplate.convertAndSend(props.exchange(), props.routingKey(), message);
        log.info("E-mail {} publicado na fila {}", message.emailId(), props.queue());
    }
}
